package com.techelevator.tenmo.entity;

import javax.persistence.*;


@Entity
@Table(name="transfer")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private int transfer_id;
    @Column(name = "amount")
    private double amount;
    @ManyToOne
    @JoinColumn(name = "account_to", referencedColumnName = "account_id")
    private Account account_to;
    @ManyToOne
    @JoinColumn(name = "account_from", referencedColumnName = "account_id")
    private Account account_from;
    @ManyToOne
    @JoinColumn(name = "transfer_status_id")
    private TransferStatus transferStatus;
    @ManyToOne
    @JoinColumn(name = "transfer_type_id")
    private TransferType transferType;

    public int getTransfer_id() {
        return transfer_id;
    }

    public void setTransfer_id(int transfer_id) {
        this.transfer_id = transfer_id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Account getAccount_to() {
        return account_to;
    }

    public void setAccount_to(Account account_to) {
        this.account_to = account_to;
    }

    public Account getAccount_from() {
        return account_from;
    }

    public void setAccount_from(Account account_from) {
        this.account_from = account_from;
    }

    public TransferStatus getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(TransferStatus transferStatus) {
        this.transferStatus = transferStatus;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public void setTransferType(TransferType transferType) {
        this.transferType = transferType;
    }
}