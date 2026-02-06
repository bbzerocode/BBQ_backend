package com.bbqpos.backend.service;

import com.bbqpos.backend.dto.finance.*;
import com.bbqpos.backend.model.*;
import com.bbqpos.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class FinanceService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;
    private final ReceivableRepository receivableRepository;
    private final PayableRepository payableRepository;

    @Autowired
    public FinanceService(AccountRepository accountRepository, TransactionRepository transactionRepository, CustomerRepository customerRepository, SupplierRepository supplierRepository, ReceivableRepository receivableRepository, PayableRepository payableRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.supplierRepository = supplierRepository;
        this.receivableRepository = receivableRepository;
        this.payableRepository = payableRepository;
    }

    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountDto::new)
                .collect(Collectors.toList());
    }

    public AccountDto getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return new AccountDto(account);
    }

    @Transactional
    public AccountDto updateAccountBalance(Long id, BigDecimal balance) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal oldBalance = account.getBalance();
        account.setBalance(balance);
        Account updatedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(balance.compareTo(oldBalance) > 0 ? Transaction.Type.INCOME : Transaction.Type.EXPENSE);
        transaction.setAmount(balance.subtract(oldBalance).abs());
        transaction.setDescription("Initial balance setup");
        transactionRepository.save(transaction);

        return new AccountDto(updatedAccount);
    }

    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(TransactionDto::new)
                .collect(Collectors.toList());
    }

    public List<ReceivableDto> getAllReceivables() {
        return receivableRepository.findByStatus(Receivable.Status.PENDING).stream()
                .map(ReceivableDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReceivableDto payReceivable(Long id, String paymentMethod, BigDecimal amount) {
        Receivable receivable = receivableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receivable not found"));

        if (receivable.getStatus() != Receivable.Status.PENDING) {
            throw new RuntimeException("Receivable is already paid");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero");
        }

        BigDecimal remainingAmount = receivable.getAmount().subtract(receivable.getPaidAmount());
        if (amount.compareTo(remainingAmount) > 0) {
            throw new RuntimeException("Payment amount cannot exceed remaining amount");
        }

        receivable.setPaidAmount(receivable.getPaidAmount().add(amount));

        if (receivable.getPaidAmount().compareTo(receivable.getAmount()) >= 0) {
            receivable.setStatus(Receivable.Status.PAID);
            receivable.setPaidAt(LocalDateTime.now());
        }

        Receivable updatedReceivable = receivableRepository.save(receivable);

        Account.Type accountType = getAccountTypeFromPaymentMethod(paymentMethod);
        Account account = accountRepository.findByType(accountType)
                .orElseThrow(() -> new RuntimeException("Account not found for payment method: " + paymentMethod));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(Transaction.Type.INCOME);
        transaction.setAmount(amount);
        transaction.setDescription("Receivable payment from " + receivable.getCustomer().getName());
        transactionRepository.save(transaction);

        return new ReceivableDto(updatedReceivable);
    }

    public List<PayableDto> getAllPayables() {
        return payableRepository.findByStatus(Payable.Status.PENDING).stream()
                .map(PayableDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public PayableDto payPayable(Long id, String paymentMethod, BigDecimal amount) {
        Payable payable = payableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payable not found"));

        if (payable.getStatus() != Payable.Status.PENDING) {
            throw new RuntimeException("Payable is already paid");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero");
        }

        BigDecimal remainingAmount = payable.getAmount().subtract(payable.getPaidAmount());
        if (amount.compareTo(remainingAmount) > 0) {
            throw new RuntimeException("Payment amount cannot exceed remaining amount");
        }

        payable.setPaidAmount(payable.getPaidAmount().add(amount));

        if (payable.getPaidAmount().compareTo(payable.getAmount()) >= 0) {
            payable.setStatus(Payable.Status.PAID);
            payable.setPaidAt(LocalDateTime.now());
        }

        Payable updatedPayable = payableRepository.save(payable);

        Account.Type accountType = getAccountTypeFromPaymentMethod(paymentMethod);
        Account account = accountRepository.findByType(accountType)
                .orElseThrow(() -> new RuntimeException("Account not found for payment method: " + paymentMethod));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance in account");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(Transaction.Type.EXPENSE);
        transaction.setAmount(amount);
        transaction.setDescription("Payable payment to " + payable.getSupplier().getName());
        transactionRepository.save(transaction);

        return new PayableDto(updatedPayable);
    }

    public void initAccounts(InitAccountsDto initAccountsDto) {
        if (accountRepository.count() > 0) {
            throw new RuntimeException("账户已初始化，不可重复设置");
        }

        BigDecimal cashAmount = initAccountsDto.getCashAmount() != null ? initAccountsDto.getCashAmount() : BigDecimal.ZERO;
        BigDecimal wechatAmount = initAccountsDto.getWechatAmount() != null ? initAccountsDto.getWechatAmount() : BigDecimal.ZERO;
        BigDecimal alipayAmount = initAccountsDto.getAlipayAmount() != null ? initAccountsDto.getAlipayAmount() : BigDecimal.ZERO;
        BigDecimal bankAmount = initAccountsDto.getBankAmount() != null ? initAccountsDto.getBankAmount() : BigDecimal.ZERO;

        Account cashAccount = new Account();
        cashAccount.setType(Account.Type.CASH);
        cashAccount.setBalance(cashAmount);

        Account wechatAccount = new Account();
        wechatAccount.setType(Account.Type.WECHAT);
        wechatAccount.setBalance(wechatAmount);

        Account alipayAccount = new Account();
        alipayAccount.setType(Account.Type.ALIPAY);
        alipayAccount.setBalance(alipayAmount);

        Account bankAccount = new Account();
        bankAccount.setType(Account.Type.BANK);
        bankAccount.setBalance(bankAmount);

        accountRepository.saveAll(Arrays.asList(cashAccount, wechatAccount, alipayAccount, bankAccount));
    }

    // 保持原有方法的兼容性
    public void initAccounts() {
        initAccounts(new InitAccountsDto());
    }

    @Transactional
    public TransactionDto createTransaction(String type, BigDecimal amount, String category, String paymentMethod, String description, LocalDateTime date) {
        System.out.println("FinanceService.createTransaction called with date: " + date);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        if ("未付".equals(paymentMethod)) {
            if ("income".equals(type)) {
                // 创建应收账款
                Receivable receivable = new Receivable();
                receivable.setAmount(amount);
                receivable.setPaidAmount(BigDecimal.ZERO);
                receivable.setStatus(Receivable.Status.PENDING);
                receivable.setCategory(category);
                receivable.setDescription(description);
                receivable.setCreatedAt(date != null ? date : LocalDateTime.now());
                // 设置默认客户
                Customer defaultCustomer = customerRepository.findByName("默认客户").orElse(null);
                if (defaultCustomer == null) {
                    defaultCustomer = new Customer();
                    defaultCustomer.setName("默认客户");
                    defaultCustomer.setPhone("12345678900");
                    defaultCustomer.setCreatedAt(LocalDateTime.now());
                    customerRepository.save(defaultCustomer);
                }
                receivable.setCustomer(defaultCustomer);
                receivableRepository.save(receivable);

                return new TransactionDto(receivable);
            } else {
                // 创建应付账款
                Payable payable = new Payable();
                payable.setAmount(amount);
                payable.setPaidAmount(BigDecimal.ZERO);
                payable.setStatus(Payable.Status.PENDING);
                payable.setCategory(category);
                payable.setDescription(description);
                payable.setCreatedAt(date != null ? date : LocalDateTime.now());
                // 设置默认供应商
                Supplier defaultSupplier = supplierRepository.findByName("默认供应商").orElse(null);
                if (defaultSupplier == null) {
                    defaultSupplier = new Supplier();
                    defaultSupplier.setName("默认供应商");
                    defaultSupplier.setContactPerson("默认联系人");
                    defaultSupplier.setPhone("12345678900");
                    defaultSupplier.setCreatedAt(LocalDateTime.now());
                    supplierRepository.save(defaultSupplier);
                }
                payable.setSupplier(defaultSupplier);
                payableRepository.save(payable);

                return new TransactionDto(payable);
            }
        } else {
            // 更新账户余额并创建交易记录
            Account.Type accountType = getAccountTypeFromPaymentMethod(paymentMethod);
            Account account = accountRepository.findByType(accountType)
                    .orElseThrow(() -> new RuntimeException("Account not found for payment method: " + paymentMethod));

            if ("income".equals(type)) {
                account.setBalance(account.getBalance().add(amount));
            } else {
                if (account.getBalance().compareTo(amount) < 0) {
                    throw new RuntimeException("Insufficient balance in account");
                }
                account.setBalance(account.getBalance().subtract(amount));
            }
            accountRepository.save(account);

            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setType("income".equals(type) ? Transaction.Type.INCOME : Transaction.Type.EXPENSE);
            transaction.setAmount(amount);
            transaction.setCategory(category);
            transaction.setDescription(description);
            transaction.setCreatedAt(date != null ? date : LocalDateTime.now());
            transactionRepository.save(transaction);

            return new TransactionDto(transaction);
        }
    }

    private Account.Type getAccountTypeFromPaymentMethod(String paymentMethod) {
        if (paymentMethod == null) {
            return Account.Type.CASH;
        }
        
        switch (paymentMethod.toLowerCase()) {
            case "cash":
            case "现金支付":
            case "现金":
                return Account.Type.CASH;
            case "wechat":
            case "wechat pay":
            case "微信支付":
            case "微信":
                return Account.Type.WECHAT;
            case "alipay":
            case "ali pay":
            case "支付宝":
                return Account.Type.ALIPAY;
            case "bank":
            case "bank card":
            case "银行卡":
                return Account.Type.BANK;
            default:
                return Account.Type.CASH;
        }
    }

}
