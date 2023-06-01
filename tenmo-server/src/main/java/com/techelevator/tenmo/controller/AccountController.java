package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.entity.Account;
import com.techelevator.tenmo.entity.Transfer;
import com.techelevator.tenmo.entity.User;
import com.techelevator.tenmo.service.*;
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
    @Autowired
    TransferStatusService transferStatusService;
    @Autowired
    TransferTypeService transferTypeService;

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
    @ResponseStatus(HttpStatus.CREATED )
    @PostMapping(value = "makeATransfer", params = {"account_from","account_to","amount"})
    public Transfer makeATransfer(@RequestParam int account_from,
                                                             @RequestParam int account_to,
                                                             @RequestParam double amount,
                                                             Principal principal){
        if(account_from==account_to){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Couldn't transfer to same account.");
        }
        Account from = accountService.getAccountByID(account_from);
        if (from.getBalance()<amount){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not enough founding ");
        }
        Account to =accountService.getAccountByID(account_to);
        Transfer transfer = new Transfer();
        transfer.setAccount_to(to);
        transfer.setAccount_from(from);
        if(from.getUser().getUsername().equals(principal.getName())){
            transfer.setTransferType(transferTypeService.findByDescription("Send"));
        }else {
            transfer.setTransferType(transferTypeService.findByDescription("Request"));
        }
        transfer.setTransferStatus(transferStatusService.findByDescription("Pending"));
        return transferService.createTransfer(transfer);
    }
}
