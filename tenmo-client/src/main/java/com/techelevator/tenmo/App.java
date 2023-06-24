package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AuthenticatedUser currentUser;
    private AccountService accountService;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        currentUser=null;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else {
            accountService = new AccountService(currentUser);
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 6) {
                depositBucks();
            } else if (menuSelection == 7) {
                withdrawBucks();
            } else if (menuSelection == 8) {
                createNewAccount();
            } else if (menuSelection == 9) {
                currencyConversionMenu();
            } else if (menuSelection == 0) {
                loginMenu();
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }
    // Added
    private void currencyConversionMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printCurrencyConversionMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrencyCodes();
            }
            else if  (menuSelection == 2) {
                convertCurrency();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        AccountService accountService = new AccountService(currentUser);
        List<Account> accounts = accountService.getAccount();
        consoleService.printAllAccounts(accounts);
    }

    private void viewTransferHistory() {
        List<Account> accountList = accountService.getAccount();
        consoleService.printAllAccounts(accountList);
        int accountID = consoleService.promptForInt("Please select an account id: ");
        if (!checkForAccountID(accountID, accountList)) {
            System.out.println("Invalid account ID");
            return;
        }
        List<Transfer> transfers = accountService.getAllTransferByAccount(accountID);
        consoleService.printTransferHistory(transfers);
        int transferIdChoice = consoleService.promptForInt("\nPlease enter transfer ID to view details (0 to cancel): ");
        if (checkForZero(transferIdChoice)) {
            return;
        }
        transfers = transfers.stream().filter(s -> s.getTransfer_id() == transferIdChoice).collect(Collectors.toList());
        consoleService.printTransferDetails(transfers.get(0));
    }

    private void viewPendingRequests() {
        String description = "";
        List<Account> accountList = accountService.getAccount();
        consoleService.printAllAccounts(accountList);
        int accountID = consoleService.promptForInt("Please select an account id: ");
        if (!checkForAccountID(accountID, accountList)) {
            System.out.println("Invalid account ID");
            return;
        }
        List<Transfer> transferList = accountService.getAllTransferByAccount(accountID);
        transferList = accountService.getTransferByTransferStatusAndTransferType(transferList, "Pending", "Request");
        transferList = transferList.stream().filter(s -> s.getAccount_from().getAccount_id() == accountID).collect(Collectors.toList());
        consoleService.printPendingTransfer(transferList);
        if (transferList.size() == 0) {
            return;
        }
        int transferID = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
        if (checkForZero(transferID)) {
            return;
        }
        consoleService.printApproveOrReject();
        int userChoice = consoleService.promptForInt("Please choose an option: ");
        try {
            switch (userChoice) {
                case 1:
                    description = "Approved";
                    consoleService.printTransferDetails(accountService.comfirmtransfer(transferID, description));
                    break;
                case 2:
                    description = "Rejected";
                    consoleService.printTransferDetails(accountService.comfirmtransfer(transferID, description));
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void sendBucks() {
        List<Account> senderAccountList = accountService.getAccount();
        List<User> users = accountService.getListUsers();
        consoleService.printAllAccounts(senderAccountList);
        int senderAccountId = consoleService.promptForInt("Enter which Account to send funds form: ");
        consoleService.printAllUser(users);
        int userId = consoleService.promptForInt("User ID: ");
        List<Account> recipientAccountList = accountService.getAccountByUserId(userId);
        consoleService.printAllAccounts(recipientAccountList);
        int recipientAccountId = consoleService.promptForInt("Enter the recipient's Account ID: ");

        double amount = consoleService.promptForBigDecimal("Enter the amount to transfer: ").doubleValue();
        try {
            Transfer transfer = accountService.transferFunds(senderAccountId,
                    recipientAccountId, amount);
            consoleService.printTransferDetails(transfer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void requestBucks() {
        List<Account> accountList = accountService.getAccount();
        List<User> userList = accountService.getListUsers();
        consoleService.printAllAccounts(accountList);
        int receiverAccountId = consoleService.promptForInt("Enter which Account to add funds to: ");
        consoleService.printAllUser(userList);
        int userId = consoleService.promptForInt("Enter which user to request funds from by ID: ");
        List<Account> senderAccountList = accountService.getAccountByUserId(userId);
        consoleService.printRequestAccounts(senderAccountList);
        int senderAccountId = consoleService.promptForInt("Enter the senders Account ID: ");
        double amount = consoleService.promptForBigDecimal("Enter the amount to request: ").doubleValue();
        try {
            Transfer transfer = accountService.transferFunds(senderAccountId, receiverAccountId, amount);
            consoleService.printTransferDetails(transfer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void withdrawBucks() {
        List<Account> accountList = accountService.getAccount();
        consoleService.printAllAccounts(accountList);
        int accountID = consoleService.promptForInt("Please select an account id (0 to cancel): ");
        if (checkForZero(accountID)) {
            return;
        }
        if (!checkForAccountID(accountID, accountList)) {
            System.out.println("Invalid account ID");
            return;
        }
        double amount = consoleService.promptForBigDecimal("Enter withdraw amount: ").doubleValue();
        try {
            accountService.withdrawMoney(accountID, amount);
            consoleService.printAccount(accountService.getAccount(accountID));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void depositBucks() {
        List<Account> accountList = accountService.getAccount();
        consoleService.printAllAccounts(accountList);
        int accountID = consoleService.promptForInt("Please select an account id (0 to cancel): ");
        if (checkForZero(accountID)) {
            return;
        }
        if (!checkForAccountID(accountID, accountList)) {
            System.out.println("Invalid account ID");
            return;
        }
        double amount = consoleService.promptForBigDecimal("Enter deposit amount: ").doubleValue();
        try {
            accountService.depositMoney(accountID, amount);
            consoleService.printAccount(accountService.getAccount(accountID));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private boolean checkForZero(double number) {
        return number == 0;
    }

    private boolean checkForAccountID(int accountID, List<Account> accountList) {
        return accountList.stream().filter(s -> s.getAccount_id() == accountID).collect(Collectors.toList()).size() > 0;
    }

    private boolean checkForTransferID(int transferID, List<Transfer> transferList) {
        return transferList.stream().filter(s -> s.getTransfer_id() == transferID).collect(Collectors.toList()).size() > 0;
    }

    private void viewCurrencyCodes() {
        accountService.viewCurrencyCodes();
    }

    private void createNewAccount(){
        String userInput = null;

        while (true){
           userInput=consoleService.promptForString("Would you like to create a new account? Type Yes to confirm, No to cancel : ");
           if(userInput.equalsIgnoreCase("yes")){
               consoleService.printAccount(accountService.createNewAccount());
               return;
           }
           if(userInput.equalsIgnoreCase("no")){
               return;
           }
           System.out.println("Invalid Input");
        }
    }
    private void convertCurrency(){
        String sourceCurrency = consoleService.promptForCurrencyCode("Enter source currency: ");
        String targetCurrency = consoleService.promptForCurrencyCode("Enter target currency: ");
        BigDecimal amount = consoleService.promptForBigDecimal("Enter the amount: ");
        accountService.convertCurrency(amount, sourceCurrency, targetCurrency);
    }
}