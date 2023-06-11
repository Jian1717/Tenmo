package com.techelevator.tenmo.service;

import com.techelevator.tenmo.entity.User;
import com.techelevator.tenmo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class UserService {

    @Autowired UserRepository userRepository;
    public UserService(){

    }
    public User getCurrentUser(Principal principal){
        return userRepository.findByUsername(principal.getName());
    }
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public User getUserByID(int userID){
        return userRepository.findById(userID);
    }
}
