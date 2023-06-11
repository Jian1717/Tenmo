package com.techelevator.tenmo.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transfer {
    private int transferId;
    private double amount;
    private Account accountFrom;
    private Account accountTo;
}