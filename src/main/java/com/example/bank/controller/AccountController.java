package com.example.bank.controller;

import com.example.bank.dto.AccountResponse;
import com.example.bank.dto.AmountRequest;
import com.example.bank.dto.CreateAccountRequest;
import com.example.bank.dto.TransferRequest;
import com.example.bank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AccountResponse.from(accountService.createAccount(request)));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts().stream()
                .map(AccountResponse::from)
                .toList();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{number}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String number) {
        return ResponseEntity.ok(AccountResponse.from(accountService.getAccount(number)));
    }

    @PostMapping("/{number}/deposit")
    public ResponseEntity<AccountResponse> deposit(@PathVariable String number,
                                                 @Valid @RequestBody AmountRequest request) {
        return ResponseEntity.ok(AccountResponse.from(accountService.deposit(number, request.getAmount())));
    }

    @PostMapping("/{number}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable String number,
                                                    @Valid @RequestBody AmountRequest request) {
        return ResponseEntity.ok(AccountResponse.from(accountService.withdraw(number, request.getAmount())));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest request) {
        accountService.transfer(request.getFromNumber(), request.getToNumber(), request.getAmount());
        return ResponseEntity.ok().build();
    }
}
