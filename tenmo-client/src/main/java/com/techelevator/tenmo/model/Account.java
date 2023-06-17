package com.techelevator.tenmo.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account {
    private int account_id;
    private double balance;

    private User user;
}