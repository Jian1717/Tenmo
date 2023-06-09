package com.techelevator.tenmo.service;

import com.techelevator.tenmo.entity.Account;
import com.techelevator.tenmo.entity.User;
import com.techelevator.tenmo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    @Autowired AccountRepository accountRepository;

    public AccountService() {
    }
    public List<Account> getAccountByUser(User user){
        return accountRepository.findAllByUser(user);
    }
    public Account getAccountByID(int accountId){
        return accountRepository.findById(accountId);
    }
    public Account saveAccount(Account account){
        return  accountRepository.save(account);
    }

    public Account updateAccountBalance(int id,double amount){
       return accountRepository.updateBalance(id, amount) ;
    }
}
