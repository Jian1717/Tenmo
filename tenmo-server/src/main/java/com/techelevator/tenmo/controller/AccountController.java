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
    /**return list of account that associated with current login user*/
    @GetMapping("getUserAccount")
    public List<Account> getUserAccount(Principal principal){
        return accountService.getAccountByUser(userService.getCurrentUser(principal));
    }
    /**return the account that match with searching account*/
    @GetMapping(value = "getUserAccount",params = "account_id")
    public Account getUserAccountByID(@RequestParam int account_id){
        return accountService.getAccountByID(account_id);
    }
    /**return list of all user that registered in the database*/
    @GetMapping("getAllUser")
    public List<User> getAllUser(){
        return userService.getAllUser();
    }
    /**return current login user*/
    @GetMapping("getCurrentUser")
    public User getCurrentUser(Principal principal){
        return userService.getCurrentUser(principal);
    }
    /**return list of transfers that matching with searching account*/
    @GetMapping("getAllTransferForAccount")
    public List<Transfer> getAllTransferForAccount(@RequestParam int account_id){
        return transferService.findAllTransferOfAccount(account_id);
    }
    /**return transfer that matching with searching transfer id*/
    @GetMapping("getTransfer/{id}")
    public Transfer getTransferById(@PathVariable int transferID){
        return transferService.findById(transferID);
    }
    /**created a new transfer by account_from, account_to and  transfer amount.
     * return created transfer*/
    @ResponseStatus(HttpStatus.CREATED )
    @PostMapping(value = "makeATransfer", params = {"account_from","account_to","amount"})
    public Transfer makeATransfer(@RequestParam int account_from,
                                                             @RequestParam int account_to,
                                                             @RequestParam double amount,
                                                             Principal principal){
        //check if it's trying to send or request money to same user account
        if(account_from==account_to){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Couldn't transfer to same account.");
        }
        Account from = accountService.getAccountByID(account_from);
        Account to =accountService.getAccountByID(account_to);
        //check if current login user is either the sender or receiver
        if(!(verifyCurrentUser(from,principal)||verifyCurrentUser(to,principal))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not an authorized account user to make transfer");
        }
        Transfer transfer = new Transfer();
        transfer.setAccount_to(to);
        transfer.setAccount_from(from);
        //check if current login user is sender.
        if(verifyCurrentUser(from,principal)){
            //check is the current user has enough money to send the money
            if (from.getBalance()<amount){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not enough founding ");
            }
            //mark transfer type to send
            transfer.setTransferType(transferTypeService.findByDescription("Send"));
        }else {
            //mark transfer type to request
            transfer.setTransferType(transferTypeService.findByDescription("Request"));
        }
        //transfer status is always set to Pending in default
        transfer.setTransferStatus(transferStatusService.findByDescription("Pending"));
        transfer.setAmount(amount);
        //save new transfer to database
        return transferService.saveTransfer(transfer);
    }
    /**Change a pending transfer to either approved or rejected state.
     * if the transfer is approved, deduct or add money to corresponding account.
     * return an executed transfer*/
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "confirmTransfer")
    public Transfer confirmTransfer(@RequestParam int transfer_id,
                                        @RequestParam String transferStatus,
                                    Principal principal){
        Transfer transfer= transferService.findById(transfer_id);
        double amount=transfer.getAmount();
        double balance;
        //check if transfer is in pending state
        if(!transfer.getTransferStatus().getDescription().equals("Pending")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested transfer is already ratify");
        }
        //check if current login user are the sender of the Transfer
        if(verifyCurrentUser(transfer.getAccount_from(),principal)) {
            switch (transferStatus) {
                    case "Rejected":
                        //mark transfer as rejected state and do nothing with account balance
                        transfer.setTransferStatus(transferStatusService.findByDescription("Rejected"));
                        return transferService.saveTransfer(transfer);
                    case "Approved":
                        //check if current user has enough balance to make transfer.
                            balance = transfer.getAccount_from().getBalance();
                            if (amount > balance) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough founding ");
                            } else {
                                //take money out from sender account and add money to receiver account.
                                transfer.getAccount_from().setBalance(transfer.getAccount_from().getBalance() - transfer.getAmount());
                                transfer.getAccount_to().setBalance(transfer.getAccount_to().getBalance()+transfer.getAmount());
                            }
                            //change transfer state to approved
                            transfer.setTransferStatus(transferStatusService.findByDescription("Approved"));
                            return transferService.saveTransfer(transfer);
                    default:
                        //adding the default case to switch statement.
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid transfer status state");
            }
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not an authorized user to approve or reject this transfer ");
        }
    }

    /*
        Following is Ezequiel's approach on checking balance using the existing method of this class verifyCurrentUser
     */

    @GetMapping("checkBalance/{accountId}")
    public double checkBalance(@PathVariable int accountId, Principal principal) {
        Account account = accountService.getAccountByID(accountId);

        if (account != null && verifyCurrentUser(account, principal)) {
            return account.getBalance();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to account");
        }
    }

    /**verify current user are the owner for the account.*/
    private boolean verifyCurrentUser(Account account, Principal principal){
        return account.getUser().getUsername().equals(principal.getName());
    }
}
