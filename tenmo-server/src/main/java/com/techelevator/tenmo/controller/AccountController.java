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
    @GetMapping("user/account")
    public List<Account> getUserAccount(Principal principal){
        return accountService.getAccountByUser(userService.getCurrentUser(principal));
    }
    /**return list of account that associated with searching user*/
    @GetMapping("user/{id}/account")
    public List<Account> getUserAccountByUserID(@PathVariable int id){
        return accountService.getAccountByUser(userService.getUserByID(id));
    }
    /**return the account that match with searching account*/
    @GetMapping(value = "account/{id}")
    public Account getAccountByID(@PathVariable int id){
        return accountService.getAccountByID(id);
    }

    /**return list of all user that registered in the database*/
    @GetMapping("user/userList")
    public List<User> getAllUser(){
        return userService.getAllUser();
    }

    /**return current login user*/
    @GetMapping("user/currentUser")
    public User getCurrentUser(Principal principal){
        return userService.getCurrentUser(principal);
    }

    /**return list of transfers that matching with searching account*/
    @GetMapping("account/{id}/getAllTransfer")
    public List<Transfer> getAllTransferForAccount(@PathVariable  int id){
        return transferService.findAllTransferOfAccount(id);
    }

    /**return transfer that matching with searching transfer id*/
    @GetMapping("transfer/{id}")
    public Transfer getTransferById(@PathVariable int id){
        return transferService.findById(id);
    }


    /**Adding a new account to current logged-in user*/
    @ResponseStatus(HttpStatus.CREATED )
    @PostMapping("user/createNewAccount")
    public Account createNewAccount(Principal principal){
        User user=userService.getCurrentUser(principal);
        Account account= new Account();
        account.setBalance(1000);
        account.setUser(user);
        return accountService.saveAccount(account);
    }

    /**created a new transfer by account_from, account_to and  transfer amount.
     * return created transfer*/
    @ResponseStatus(HttpStatus.CREATED )
    @PostMapping(value = "transfer/createNewTransfer", params = {"account_from","account_to","amount"})
    public Transfer makeATransfer(@RequestParam int account_from,
                                                             @RequestParam int account_to,
                                                             @RequestParam double amount,
                                                             Principal principal){
        //check if it's trying to send or request money from/to same user account
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
        transfer.setTransferStatus(transferStatusService.findByDescription("Pending"));
        //check if current login user is the sender.
        if(verifyCurrentUser(from,principal)){
            //check is the current user has enough money to send the money
            if (from.getBalance()<amount){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not enough founding ");
            }
            //mark transfer type to send
            transfer.setTransferType(transferTypeService.findByDescription("Send"));
            transfer.setAmount(amount);
            transfer= transferService.saveTransfer(transfer);
            confirmTransfer(transfer.getTransfer_id(),"Approved",principal);
            return transfer;
        }else {
            //mark transfer type to request
            transfer.setTransferType(transferTypeService.findByDescription("Request"));
            transfer.setAmount(amount);
            return transferService.saveTransfer(transfer);
        }

        //save new transfer to database

    }
    /**Change a pending transfer to either approved or rejected state.
     * if the transfer is approved, deduct or add money to corresponding account.
     * return an executed transfer*/
    @ResponseStatus(HttpStatus.OK)
    @PutMapping(value = "transfer/confirmTransfer/{transfer_id}")
    public Transfer confirmTransfer(@PathVariable int transfer_id,
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
                        //mark transfer as rejected and do nothing with account balance
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
    /**adding money into an account*/
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("account/{id}/depositMoney")
    public Account addMoneyToAccount(@PathVariable int id,
                                      @RequestParam double amount){
        if(amount<=0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please enter positive value for amount.");
        }
        amount=accountService.getAccountByID(id).getBalance()+amount;
        return accountService.updateAccountBalance(id,amount);
    }
    /**subtract money from an account*/
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("account/{id}/withdrawMoney")
    public Account subtractMoneyFromAccount(@PathVariable int id,
                                      @RequestParam double amount) {
        double balance = accountService.getAccountByID(id).getBalance();
        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please enter positive value for amount.");
        }
        if (balance < amount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough balance");
        }
        balance = balance - amount;
        return accountService.updateAccountBalance(id, balance);
    }
    /**verify current user are the owner for the account.*/
    private boolean verifyCurrentUser(Account account, Principal principal){
        return account.getUser().getUsername().equals(principal.getName());
    }

}
