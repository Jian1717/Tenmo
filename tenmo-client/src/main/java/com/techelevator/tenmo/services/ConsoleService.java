package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("6: Deposit TE bucks");
        System.out.println("7: Withdraw TE bucks");
        System.out.println("8: Create new account");
        System.out.println("9: Currency conversion menu");

        System.out.println("0: Exit");
        System.out.println();
    }
    //added
    public void printCurrencyConversionMenu(){
        System.out.println();
        System.out.println("1: View currency codes");
        System.out.println("2: Exchange");
        System.out.println("0: Exit");
        System.out.println();
    }
    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }
    public String promptForString(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                throw new IllegalArgumentException("Input cannot be empty.");
            }
            return input;
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return promptForString(prompt);
        }
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }
    //added
    public String promptForCurrencyCode(String prompt) {
        System.out.print(prompt);
        String currencyCode = "";

        while (currencyCode.isEmpty()) {
        //    System.out.print(message);
            currencyCode = scanner.nextLine().trim().toLowerCase();
            if (currencyCode.isEmpty()) {
                System.out.println("Currency code cannot be empty.");
            }
        }

        return currencyCode;
    }


    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printAllUser(List<User> userList) {
        System.out.println();
        System.out.println("-------------------------------------------\n" +
                "Users\n" +
                "ID          Name\n" +
                "-------------------------------------------");
        for (User user : userList) {
            System.out.print(user.getId() + "      ");
            System.out.println(user.getUsername());
        }
    }

    public void printAllAccounts(List<Account> accountList) {
        System.out.println();
        System.out.println("----------------------------------------------\n" + " Account");
        System.out.printf(" %-20s  %-20s  %n", "Account ID", "Balance");
        System.out.println("----------------------------------------------");
        accountList.forEach(s -> System.out.printf(" %-20s  %-20s %n", s.getAccount_id(), s.getBalance()));
        System.out.println("----------------------------------------------");
    }

    public void printAccount(Account account) {
        System.out.println();
        System.out.println("----------------------------------------------\n" + " Account");
        System.out.printf(" %-20s  %-20s  %n", "Account ID", "Balance");
        System.out.println("----------------------------------------------");
        System.out.printf(" %-20s  %-20s %n", account.getAccount_id(), account.getBalance());
        System.out.println("----------------------------------------------");
    }
    public void printRequestAccounts(List<Account> accountList) {
        System.out.println();
        System.out.println("----------------------------------------------\n" + " Account Requesting");
        System.out.printf(" %-20s  %n", "Account ID");
        System.out.println("----------------------------------------------");
        accountList.forEach(s -> System.out.printf(" %-20s %n", s.getAccount_id()));
        System.out.println("----------------------------------------------");
    }

    public void printPendingTransfer(List<Transfer> transferList) {
        System.out.println();
        if (transferList.size() > 0) {
            System.out.println("----------------------------------------------\n" + " Pending Transfer");
            System.out.printf(" %-20s  %-20s  %-20s %n", "ID:", "To:", "Amount:");
            System.out.println("----------------------------------------------");
            transferList.forEach(s -> System.out.printf(" %-20s  %-20s  %-20s %n", s.getTransfer_id(), s.getAccount_to().getAccount_id(), s.getAmount()));
            System.out.println("----------------------------------------------");
        } else {
            System.out.println("No pending transfer at the moment");
        }
    }

    public void printApproveOrReject() {
        System.out.println();
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject");
        System.out.println("----------------------------------");
    }

    public void printTransferDetails(Transfer transfer) {
        System.out.println();
        System.out.println("----------------------------------------------");
        System.out.println("Transfer Detail");
        System.out.println("----------------------------------------------");
        System.out.printf(" %-20s  %-20s %n", "ID: ", transfer.getTransfer_id());
        System.out.printf(" %-20s  %-20s %n", "From: ", transfer.getAccount_from().getAccount_id());
        System.out.printf(" %-20s  %-20s %n", "To: ", transfer.getAccount_to().getAccount_id());
        System.out.printf(" %-20s  %-20s %n", "Type: ", transfer.getTransferType().getDescription());
        System.out.printf(" %-20s  %-20s %n", "Status: ", transfer.getTransferStatus().getDescription());
        System.out.printf(" %-20s  %-20s %n", "Amount: ", transfer.getAmount());
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }


    public void printTransferHistory(List<Transfer> transferList) {
        System.out.println();
        if (transferList.size() > 0) {
            System.out.println("--------------------------------------------------------------------------------------------\n");
            System.out.println("Transfer\n");
            System.out.printf(" %-20s  %-20s  %-20s  %-20s %n", "ID:", "From:", "To:", "Amount:");
            System.out.println("--------------------------------------------------------------------------------------------");
            transferList.forEach(s -> System.out.printf(" %-20s %-20s %-20s %-20s %n", s.getTransfer_id(), s.getAccount_from().getUser().getUsername()+"|"+s.getAccount_from().getAccount_id(),s.getAccount_to().getUser().getUsername()+"|"+s.getAccount_to().getAccount_id(), s.getAmount()));
            System.out.println("--------------------------------------------------------------------------------------------");
        } else {
            System.out.println("No transfer history at the moment");
        }
    }

}


