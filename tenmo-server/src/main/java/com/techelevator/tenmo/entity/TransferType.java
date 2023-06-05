package com.techelevator.tenmo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "transfer_type")
public class TransferType{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_type_id")
    private int transfer_type_id;

    @Column(name = "transfer_type_desc")
    private String description;

    @OneToMany(mappedBy = "transferType")
    @JsonIgnore
    private Set<Transfer> transferSet;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Transfer> getTransferSet() {
        return transferSet;
    }

    public void setTransferSet(Set<Transfer> transferSet) {
        this.transferSet = transferSet;
    }

    public int getTransfer_type_id() {
        return transfer_type_id;
    }

    public void setTransfer_type_id(int transfer_type_id) {
        this.transfer_type_id = transfer_type_id;
    }
}
