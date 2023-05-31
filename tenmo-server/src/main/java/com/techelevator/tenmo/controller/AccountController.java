package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.entity.Account;
import com.techelevator.tenmo.entity.Transfer;
import com.techelevator.tenmo.entity.User;
import com.techelevator.tenmo.service.AccountService;
import com.techelevator.tenmo.service.TransferService;
import com.techelevator.tenmo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
public class AccountController {
    @Autowired
    AccountService accountService;
    @Autowired
    UserService userService;
    @Autowired
    TransferService transferService;
    @GetMapping("getUserAccount")
    public List<Account> getUserAccount(@RequestBody User user){
        return accountService.getAccountByUser(user);
    }
    @GetMapping(value = "getUserAccount",params = "account_id")
    public Account getUserAccountByID(@RequestParam int account_id){
        return accountService.getAccountByID(account_id);
    }
    @GetMapping("getAllUser")
    public List<User> getAllUser(){
        return userService.getAllUser();
    }
    @PreAuthorize("hasRole('USER')")
    @PostMapping("makeATransfer")
    public Transfer makeATransfer(Transfer transfer, Principal principal){
        String username=principal.getName();
        if(transfer.getAccount_from().getUser().getUsername().equals(username)){
            if(transfer.getAccount_to().equals(transfer.getAccount_from())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Couldn't transfer to same account.");
            }else {
                if(transfer.getAccount_from().getBalance()<transfer.getAmount()){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not enough founding ");
                }else {
                return transferService.createOrUpdateTransfer(transfer);
                }
            }
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please select your own account to begin the transfer transaction.");
        }
    }
}
