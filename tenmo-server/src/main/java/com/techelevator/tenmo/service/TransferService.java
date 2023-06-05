package com.techelevator.tenmo.service;

import com.techelevator.tenmo.entity.Transfer;
import com.techelevator.tenmo.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferService {
    @Autowired
    TransferRepository transferRepository;

    public Transfer saveTransfer(Transfer transfer){
        return transferRepository.save(transfer);
    }
    public Transfer findById(int id){
        return transferRepository.findById(id);
    }
    public List<Transfer> findAllTransferOfAccount(int accountID){
        return transferRepository.findAllTransferByAccount(accountID);
    }

}
