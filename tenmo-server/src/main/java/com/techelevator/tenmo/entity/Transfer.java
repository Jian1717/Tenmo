package com.techelevator.tenmo.entity;


import javax.persistence.*;

@Entity
@Table(name="transfer", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueAccountFromAndAccountTo",columnNames = {"account_to","account_from"})
})
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private int Id;
    @Column(name = "transfer_type_id")
    private TransferType transferTypeID;
    @Column(name = "transfer_Status_id")
    private TransferStatus  transferStatusID;
    @Column(name = "account_to")
    @ManyToOne
    private Account  account_to;
    @Column(name = "account_from")
    private Account  account_from;
    @Column(name = "amount")
    private double amount;
}
