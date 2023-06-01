package com.techelevator.tenmo.service;

import com.techelevator.tenmo.entity.TransferType;
import com.techelevator.tenmo.repository.TransferTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferTypeService {
    @Autowired
    TransferTypeRepository transferTypeRepository;

    public TransferType findByDescription(String description){
        return transferTypeRepository.findByDescription(description);
    }
}
