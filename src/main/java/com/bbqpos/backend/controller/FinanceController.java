package com.bbqpos.backend.controller;

import com.bbqpos.backend.dto.finance.*;
import com.bbqpos.backend.service.FinanceService;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/finance")
public class FinanceController {

    private static final Logger logger = LoggerFactory.getLogger(FinanceController.class);
    private final FinanceService financeService;

    @Autowired
    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        List<AccountDto> accounts = financeService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        AccountDto account = financeService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/accounts/{id}/balance")
    public ResponseEntity<AccountDto> updateAccountBalance(@PathVariable Long id, @Valid @RequestBody BalanceDto balanceDto) {
        AccountDto account = financeService.updateAccountBalance(id, balanceDto.getBalance());
        return ResponseEntity.ok(account);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        List<TransactionDto> transactions = financeService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/receivables")
    public ResponseEntity<List<ReceivableDto>> getAllReceivables() {
        List<ReceivableDto> receivables = financeService.getAllReceivables();
        return ResponseEntity.ok(receivables);
    }

    @PostMapping("/receivables/{id}/pay")
    public ResponseEntity<ReceivableDto> payReceivable(@PathVariable Long id, @RequestParam String paymentMethod, @RequestParam BigDecimal amount) {
        ReceivableDto receivable = financeService.payReceivable(id, paymentMethod, amount);
        return ResponseEntity.ok(receivable);
    }

    @GetMapping("/payables")
    public ResponseEntity<List<PayableDto>> getAllPayables() {
        List<PayableDto> payables = financeService.getAllPayables();
        return ResponseEntity.ok(payables);
    }

    @PostMapping("/payables/{id}/pay")
    public ResponseEntity<PayableDto> payPayable(@PathVariable Long id, @RequestParam String paymentMethod, @RequestParam BigDecimal amount) {
        PayableDto payable = financeService.payPayable(id, paymentMethod, amount);
        return ResponseEntity.ok(payable);
    }

    @PostMapping("/init-accounts")
    public ResponseEntity<Void> initAccounts(@RequestBody InitAccountsDto initAccountsDto) {
        financeService.initAccounts(initAccountsDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/transactions")
    public ResponseEntity<TransactionDto> createTransaction(@RequestParam String type, @RequestParam BigDecimal amount, @RequestParam String category, @RequestParam String paymentMethod, @RequestParam(required = false) String description, @RequestParam(required = false) String date) {
        logger.info("Creating transaction - type: {}, amount: {}, category: {}, paymentMethod: {}, description: {}, date: {}", type, amount, category, paymentMethod, description, date);
        LocalDateTime transactionDate = null;
        if (date != null && !date.isEmpty()) {
            try {
                LocalDate localDate = LocalDate.parse(date);
                transactionDate = localDate.atStartOfDay();
                logger.info("Successfully parsed date: {} to LocalDateTime: {}", date, transactionDate);
            } catch (Exception e) {
                logger.error("Failed to parse date: {}, using current time instead. Error: {}", date, e.getMessage());
                transactionDate = LocalDateTime.now();
            }
        } else {
            logger.info("Date parameter is null or empty, using current time");
            transactionDate = LocalDateTime.now();
        }
        TransactionDto transaction = financeService.createTransaction(type, amount, category, paymentMethod, description, transactionDate);
        logger.info("Transaction created successfully with createdAt: {}", transaction.getCreatedAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

}
