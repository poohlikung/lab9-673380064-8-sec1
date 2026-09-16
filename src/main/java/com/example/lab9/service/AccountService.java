package com.example.lab9.service;

import com.example.lab9.dto.CreateAccountRequest;
import com.example.lab9.exception.AccountNotFoundException;
import com.example.lab9.exception.DuplicateAccountNumberException;
import com.example.lab9.exception.InvalidAmountException;
import com.example.lab9.model.Account;
import com.example.lab9.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(CreateAccountRequest request) {
        String accountNumber = request.accountNumber().trim();
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new DuplicateAccountNumberException(accountNumber);
        }

        double initialBalance = request.balance() == null ? 0.0 : request.balance();
        if (!Double.isFinite(initialBalance)) {
            throw new InvalidAmountException("balance must be a finite number");
        }

        Account account = new Account(
                accountNumber,
                request.ownerName().trim(),
                initialBalance
        );
        return accountRepository.save(account);
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }
}
