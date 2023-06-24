package com.techelevator.tenmo.services;

//import com.techelevator.tenmo.entity.Account;
import com.techelevator.tenmo.model.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountService {
    private static final String API_BASE_URL = "http://localhost:8080/";
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser authenticatedUser;
    //
   // String apiVersion = "1";
 //   String date = "latest";
   // String endpoint = "currencies/eur/jpy";
    String exchangeUrl = "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/";

    public AccountService(AuthenticatedUser authenticatedUser) {
        this.restTemplate = new RestTemplate();
        this.authenticatedUser = authenticatedUser;
    }

    public List<Account>  getAccount() {
        String url = API_BASE_URL + "user/account";

        ResponseEntity<List<Account>> response =
                restTemplate.exchange(url, HttpMethod.GET, makeAuthEntity(),
                        new ParameterizedTypeReference<List<Account>>() {});
        List<Account> accounts = response.getBody();
        return accounts;
    }

    public Account getAccount(int accountId) {
        String url = API_BASE_URL + "account/"+accountId;
        ResponseEntity<Account> response = restTemplate.exchange(url, HttpMethod.GET,makeAuthEntity(), Account.class);
        Account account = response.getBody();

        return account;
    }

    public Transfer transferFunds(int senderAccountID,int recipientAccountId,double amount){
        String url=API_BASE_URL+"transfer/createNewTransfer";

        ResponseEntity<Transfer> response = restTemplate.exchange(url+"?account_from="+senderAccountID+"&account_to="+recipientAccountId+"&amount="+amount, HttpMethod.POST, makeAuthEntity(), Transfer.class);

        return response.getBody();
    }


    public List<Account> getAccountByUserId(int id){
        List<Account> accounts=new ArrayList<>();
                String url=API_BASE_URL+"user/"+id+"/account";
        ResponseEntity<List<Account>> response=restTemplate.exchange(url, HttpMethod.GET, makeAuthEntity(), new ParameterizedTypeReference<List<Account>>() {});
        accounts=response.getBody();

        return accounts;
    }
    public List<User> getListUsers(){
        List<User> users=new ArrayList<>();
        String url=API_BASE_URL+"user/userList";
        ResponseEntity<List<User>> response=restTemplate.exchange(url, HttpMethod.GET, makeAuthEntity(), new ParameterizedTypeReference<List<User>>() {});
        users=response.getBody();
        return users;
    }
    public List<Transfer> getAllTransferByAccount(int accountID){
        List<Transfer> transferList = new ArrayList<Transfer>();
        String url = API_BASE_URL + "account/"+accountID+"/getAllTransfer";
        ResponseEntity<List<Transfer>> response = restTemplate.exchange(url, HttpMethod.GET,makeAuthEntity(),new ParameterizedTypeReference<List<Transfer>>() {});
        transferList = response.getBody();
        return transferList;
    }

    public Transfer comfirmtransfer(int transferID,String decision){
        String url = API_BASE_URL + "transfer/confirmTransfer/"+transferID+"?transferStatus="+decision;
        ResponseEntity<Transfer> response = restTemplate.exchange(url, HttpMethod.PUT,makeAuthEntity(),Transfer.class);
        Transfer updateTransfer = response.getBody();
        return updateTransfer;
    }
    public Account depositMoney(int accountID,double amount){
        String url = API_BASE_URL + "account/"+accountID+"/depositMoney?amount="+amount;
        ResponseEntity<Account> response = restTemplate.exchange(url, HttpMethod.PUT,makeAuthEntity(),Account.class);
        return response.getBody();
    }
    public Account withdrawMoney(int accountID,double amount){
        String url = API_BASE_URL + "account/"+accountID+"/withdrawMoney?amount="+amount;
        ResponseEntity<Account> response = restTemplate.exchange(url, HttpMethod.PUT,makeAuthEntity(),Account.class);
        Account updateAccount = response.getBody();
        return updateAccount;
    }
    public Account createNewAccount(){
        String url = API_BASE_URL + "user/createNewAccount";
        ResponseEntity<Account> response = restTemplate.exchange(url, HttpMethod.POST,makeAuthEntity(),Account.class);
        return response.getBody();
    }
    public List<Transfer> getTransferByTransferStatusAndTransferType(List<Transfer> transferList , String transferStatus, String transferType ){
        return transferList.stream().filter(s->s.getTransferStatus().getDescription().equals(transferStatus)&&s.getTransferType().getDescription().equals(transferType)).collect(Collectors.toList());
    }
    //added
    public void viewCurrencyCodes(){
        String url = exchangeUrl + "currencies.json";

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                ParameterizedTypeReference.forType(Map.class)
        );
        Map<String, String> currencyMap = response.getBody();
        if (currencyMap != null) {
            for (Map.Entry<String, String> entry : currencyMap.entrySet()) {
                String code = entry.getKey();
                String name = entry.getValue();
                System.out.println("Currency code: " + code + "|| Currency name: " + name );
            }
        }
    }
    public void convertCurrency(BigDecimal amount, String sourceCurrencyCode, String targetCurrencyCode) {
        String apiUrl = exchangeUrl + "/currencies/" + sourceCurrencyCode + "/" + targetCurrencyCode + ".json";
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    null,
                    ParameterizedTypeReference.forType(Map.class)
            );

            Map<String, Object> conversionMap = response.getBody();

            if (conversionMap != null) {
                String date = (String) conversionMap.get("date");
                BigDecimal conversionRate = new BigDecimal(conversionMap.get(targetCurrencyCode).toString());

                BigDecimal convertedAmount = amount.multiply(conversionRate);

                System.out.println("Conversion result:");
                System.out.println("Date: " + date);
                System.out.println("Source Currency: " + sourceCurrencyCode);
                System.out.println("Target Currency: " + targetCurrencyCode);
                System.out.println("Amount: " + amount + " " + sourceCurrencyCode);
                System.out.println("Conversion Rate: " + conversionRate);
                System.out.println("Converted Amount: " + convertedAmount + " " + targetCurrencyCode);
            }
        }catch (Exception e){
            System.out.println("An error occurred while fetching conversion data. Please check you're entering a valid currency code");
        }
    }
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(headers);
    }
}