package com.techelevator.tenmo.repository;

import com.techelevator.tenmo.entity.Account;
import com.techelevator.tenmo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {
    List<Account> findAllByUser(User user);
    Account findById(int id);
    Account save(Account account);
}
