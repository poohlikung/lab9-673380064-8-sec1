package com.example.lab9.exception;

public class DuplicateAccountNumberException extends RuntimeException {

    public DuplicateAccountNumberException(String accountNumber) {
        super("Account number " + accountNumber + " already exists");
    }
}
