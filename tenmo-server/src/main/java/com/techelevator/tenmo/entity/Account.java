package com.techelevator.tenmo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name ="account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private int account_id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "balance")
    private double balance;
    @OneToMany(mappedBy = "account_to")
    @JsonIgnore
    private Set<Transfer> transferToSet;
    @OneToMany(mappedBy = "account_from")
    @JsonIgnore
    private Set<Transfer> transferFromSet;

    public int getAccount_id() {
        return account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Set<Transfer> getTransferToSet() {
        return transferToSet;
    }

    public void setTransferToSet(Set<Transfer> transferToSet) {
        this.transferToSet = transferToSet;
    }

    public Set<Transfer> getTransferFromSet() {
        return transferFromSet;
    }

    public void setTransferFromSet(Set<Transfer> transferFromSet) {
        this.transferFromSet = transferFromSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return account_id == account.account_id && user.equals(account.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account_id, user);
    }
}
