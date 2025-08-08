package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service

public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Optional<Account> getAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public Optional<Account> getAccountById(int accountId) {
        return accountRepository.findById(accountId);
    }

    public Account registerAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> verifyLogin(String username, String password) {
        return accountRepository.findByUsernameAndPassword(username, password);
    }
}