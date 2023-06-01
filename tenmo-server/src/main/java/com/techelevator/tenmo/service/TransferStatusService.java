package com.techelevator.tenmo.service;

import com.techelevator.tenmo.entity.TransferStatus;
import com.techelevator.tenmo.repository.TransferStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferStatusService {
    @Autowired
    TransferStatusRepository transferStatusRepository;

    public TransferStatus findByDescription(String description){
        return transferStatusRepository.findByDescription(description);
    }
}
