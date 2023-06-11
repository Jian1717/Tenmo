package com.techelevator.tenmo.services;

//import com.techelevator.tenmo.entity.Account;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(headers);
    }
}