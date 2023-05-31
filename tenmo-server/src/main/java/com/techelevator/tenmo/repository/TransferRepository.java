package com.techelevator.tenmo.repository;

import com.techelevator.tenmo.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Integer> {

    Transfer save(Transfer transfer);
}
