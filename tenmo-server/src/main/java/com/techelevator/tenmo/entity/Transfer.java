package com.techelevator.tenmo.entity;

import javax.persistence.*;

@Entity
@Table(name="transfer")
public class Transfer {
    @Id
    @Column(name = "transfer_id")
    private int Id;
    @Column(name = "transfer_type_id")
    private int transferTypeID;

    @Column(name = "transfer_Status_id")
    private int transferStatusID;
    @Column(name = "account_to")
    @ManyToOne
    private int account_to;

    @Column(name = "account_from")
    private int account_from;
    @Column(name = "amount")
    private double amount;
}
