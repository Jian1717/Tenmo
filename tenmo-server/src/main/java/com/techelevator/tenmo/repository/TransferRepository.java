package com.techelevator.tenmo.repository;

import com.techelevator.tenmo.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Integer> {

    Transfer save(Transfer transfer);

    Transfer findById(int id);

    @Query(value = "select * from transfer t where t.account_to = :accountID or t.account_from=:accountID", nativeQuery = true)
    List<Transfer> findAllTransferByAccount(@Param("accountID") int accountID);
}