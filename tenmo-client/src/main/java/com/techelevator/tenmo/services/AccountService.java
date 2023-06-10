package com.techelevator.tenmo.services;

//import com.techelevator.tenmo.entity.Account;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class AccountService {
    private static final String API_BASE_URL = "http://localhost:8080/";
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser authenticatedUser;

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
        String url = API_BASE_URL + "user/account";
        String parameter = "account_id=" + accountId;

        ResponseEntity<Account> response = restTemplate.exchange(url + "?" + parameter, HttpMethod.GET,makeAuthEntity(), Account.class);
        Account account = response.getBody();

        return account;
    }

    public Transfer transferFunds(int senderAccountID,int recipientAccountId,double amount){
        String url=API_BASE_URL+"transfer/createNewTransfer";

        ResponseEntity<Transfer> response = restTemplate.exchange(url+"?account_from="+senderAccountID+"&account_to="+recipientAccountId+"&amount="+amount, HttpMethod.POST, makeAuthEntity(), Transfer.class);

        return response.getBody();
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(headers);
    }
}