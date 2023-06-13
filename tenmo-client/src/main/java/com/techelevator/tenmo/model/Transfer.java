package com.techelevator.tenmo.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transfer {
    private int transfer_id;
    private double amount;
    private Account account_from;
    private Account account_to;
    private TransferType transferType;
    private TransferStatus transferStatus;
}