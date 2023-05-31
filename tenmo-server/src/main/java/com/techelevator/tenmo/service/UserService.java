package com.techelevator.tenmo.service;

import com.techelevator.tenmo.entity.User;
import com.techelevator.tenmo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired UserRepository userRepository;
    public UserService(){

    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }
}
