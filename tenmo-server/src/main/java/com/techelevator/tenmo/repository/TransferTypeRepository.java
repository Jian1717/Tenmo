package com.techelevator.tenmo.repository;

import com.techelevator.tenmo.entity.TransferType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferTypeRepository extends JpaRepository<TransferType, Integer> {

    TransferType findByDescription(String description);
}
