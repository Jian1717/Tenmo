package com.techelevator.tenmo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "transfer_status")
public class TransferStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_status_id")
    private int transfer_status_id;

    @Column(name = "transfer_status_desc")
    private String description;

    @OneToMany(mappedBy = "transferStatus")
    @JsonIgnore
    private Set<Transfer> transferSet;

    public int getTransfer_status_id() {
        return transfer_status_id;
    }

    public void setTransfer_status_id(int transfer_status_id) {
        this.transfer_status_id = transfer_status_id;
    }

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
}
