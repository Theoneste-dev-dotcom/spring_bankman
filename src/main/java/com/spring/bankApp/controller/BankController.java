package com.spring.bankApp.controller;

import com.spring.bankApp.model.Account;
import com.spring.bankApp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class BankController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Account account = accountService.findAccountByUsername(username);
    model.addAttribute("account", account);
    return "dashboard";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        return "register";
    }
    @PostMapping("/register")
    public String registerAccount(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            accountService.registerAccount(username, password);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @PostMapping("/deposit")
    public String deposit(@RequestParam BigDecimal amount) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountService.findAccountByUsername(username);
        accountService.deposit(account, amount);
        return "redirect:/dashboard";
    }
    @PostMapping("/withdraw")
    public String withdraw(@RequestParam BigDecimal amount, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountService.findAccountByUsername(username);
    try{
        accountService.withdraw(account, amount);
    }catch (RuntimeException e) {
        model.addAttribute("error", e.getMessage());
        model.addAttribute("account", account);
        return "withdraw";
    }

    return "redirect:/dashboard";

    }

    @GetMapping("/transactions")
    public String transactions(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountService.findAccountByUsername(username);
        model.addAttribute("transactions", accountService.getTransactionHistory(account));
        model.addAttribute("account", account);

        return "transactions";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam String  toUsername, @RequestParam BigDecimal amount, Model model) {
       String username = SecurityContextHolder.getContext().getAuthentication().getName();
       Account fromAccount = accountService.findAccountByUsername(username);

       try {
           accountService.transferAmount(fromAccount, toUsername,amount);
       } catch (RuntimeException e) {
           model.addAttribute("error", e.getMessage());
           model.addAttribute("account", fromAccount);
           return "dashboard";
       }
       return "redirect:/dashboard";

    }

}
