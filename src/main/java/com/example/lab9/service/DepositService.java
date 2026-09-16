package com.example.lab9.service;

import com.example.lab9.exception.AccountNotFoundException;
import com.example.lab9.exception.InvalidAmountException;
import com.example.lab9.model.Account;
import com.example.lab9.model.DepositTransaction;
import com.example.lab9.repository.AccountRepository;
import com.example.lab9.repository.DepositRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepositService {

    private final AccountRepository accountRepository;
    private final DepositRepository depositRepository;

    public DepositService(AccountRepository accountRepository, DepositRepository depositRepository) {
        this.accountRepository = accountRepository;
        this.depositRepository = depositRepository;
    }

    @Transactional
    public void deposit(Long accountId, Double amount) {
        if (amount == null || !Double.isFinite(amount) || amount <= 0) {
            throw new InvalidAmountException("amount must be a finite number greater than zero");
        }

        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        double newBalance = account.getBalance() + amount;
        if (!Double.isFinite(newBalance)) {
            throw new InvalidAmountException("resulting balance is too large");
        }

        account.deposit(amount);
        accountRepository.save(account);

        DepositTransaction transaction = new DepositTransaction(amount, account);
        depositRepository.save(transaction);
    }
}
