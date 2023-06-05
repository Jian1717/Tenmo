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
@PreAuthorize("isAuthenticated()")
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
    public List<Account> getUserAccount(Principal principal){
        return accountService.getAccountByUser(userService.getCurrentUser(principal));
    }
    @GetMapping(value = "getUserAccount",params = "account_id")
    public Account getUserAccountByID(@RequestParam int account_id){
        return accountService.getAccountByID(account_id);
    }
    @GetMapping("getAllUser")
    public List<User> getAllUser(){
        return userService.getAllUser();
    }
    @GetMapping("getCurrentUser")
    public User getCurrentUser(Principal principal){
        return userService.getCurrentUser(principal);
    }
    @GetMapping("getAllTransferForAccount")
    public List<Transfer> getAllTransferForAccount(@RequestParam int account_id){
        return transferService.findAllTransferOfAccount(account_id);
    }
    @GetMapping("getTransfer/{id}")
    public Transfer getTransferById(@PathVariable int transferID){
        return transferService.findById(transferID);
    }

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
        Account to =accountService.getAccountByID(account_to);
        if(!(verifyCurrentUser(from,principal)||verifyCurrentUser(to,principal))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not an authorized account user to make transfer");
        }
        Transfer transfer = new Transfer();
        transfer.setAccount_to(to);
        transfer.setAccount_from(from);
        if(verifyCurrentUser(from,principal)){
            if (from.getBalance()<amount){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not enough founding ");
            }
            transfer.setTransferType(transferTypeService.findByDescription("Send"));
        }else {
            transfer.setTransferType(transferTypeService.findByDescription("Request"));
        }
        transfer.setTransferStatus(transferStatusService.findByDescription("Pending"));
        transfer.setAmount(amount);
        return transferService.saveTransfer(transfer);
    }
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "confirmTransfer")
    public Transfer confirmTransfer(@RequestParam int transfer_id,
                                        @RequestParam String transferStatus,
                                    Principal principal){
        Transfer transfer= transferService.findById(transfer_id);
        double amount=transfer.getAmount();
        double balance;
        if(!transfer.getTransferStatus().getDescription().equals("Pending")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested transfer is already ratify");
        }
        if(verifyCurrentUser(transfer.getAccount_from(),principal)) {
            switch (transferStatus) {
                    case "Rejected":
                        transfer.setTransferStatus(transferStatusService.findByDescription("Rejected"));
                        return transferService.saveTransfer(transfer);
                    case "Approved":
                            balance = transfer.getAccount_from().getBalance();
                            if (amount > balance) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough founding ");
                            } else {
                                transfer.getAccount_from().setBalance(transfer.getAccount_from().getBalance() - transfer.getAmount());
                                transfer.getAccount_to().setBalance(transfer.getAccount_to().getBalance()+transfer.getAmount());
                            }
                            transfer.setTransferStatus(transferStatusService.findByDescription("Approved"));
                            return transferService.saveTransfer(transfer);
                    default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid transfer status state");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not an authorized user to approve or reject this transfer ");
        }
            }
    private boolean verifyCurrentUser(Account account, Principal principal){
        return account.getUser().getUsername().equals(principal.getName());
    }
}
