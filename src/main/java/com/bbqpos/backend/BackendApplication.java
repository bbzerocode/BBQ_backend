package com.bbqpos.backend;

import com.bbqpos.backend.service.AuthService;
import com.bbqpos.backend.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

    private final AuthService authService;
    private final FinanceService financeService;

    @Autowired
    public BackendApplication(AuthService authService, FinanceService financeService) {
        this.authService = authService;
        this.financeService = financeService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始化管理员用户
        authService.initAdminUser();
        // 初始化账户
        try {
            financeService.initAccounts();
        } catch (Exception e) {
            // 账户已初始化，忽略异常
            System.out.println("账户已初始化，跳过初始化步骤");
        }
    }

}
