package com.techelevator.tenmo.repository;

import com.techelevator.tenmo.entity.Account;
import com.techelevator.tenmo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {
    List<Account> findAllByUser(User user);
    Account findById(int id);
    Account save(Account account);
    @Query(value = "update account  set  balance = :amount where account_id= :id returning *" , nativeQuery = true)
    Account updateBalance(@Param("id") int id, @Param("amount") double amount);
}
