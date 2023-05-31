package com.techelevator.tenmo.service;

import com.techelevator.tenmo.entity.Transfer;
import com.techelevator.tenmo.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService {
    @Autowired
    TransferRepository transferRepository;

    public Transfer createOrUpdateTransfer(Transfer transfer){
        return transferRepository.save(transfer);
    }
}
