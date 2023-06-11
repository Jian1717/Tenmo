package com.techelevator.tenmo.repository;

import com.techelevator.tenmo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    List<User> findAll();
    User findByUsername(String username);

    User findById(int id);
}
