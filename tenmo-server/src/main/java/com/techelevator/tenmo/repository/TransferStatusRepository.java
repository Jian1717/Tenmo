package com.techelevator.tenmo.repository;

import com.techelevator.tenmo.entity.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferStatusRepository extends JpaRepository<TransferStatus, Integer> {
    TransferStatus findByDescription(String descripttion);
}
