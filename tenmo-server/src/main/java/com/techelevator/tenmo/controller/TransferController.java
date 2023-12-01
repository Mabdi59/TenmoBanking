package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
public class TransferController {

    private final TransferDAO transferDao;
    @Autowired
    private final UserDao userDao;
    public TransferController(TransferDAO transferDao, UserDao userDao){
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public ResponseEntity<?> createTransfer(@RequestBody Transfer transfer, Principal authentication) {

        String currentUsername = authentication.getName();
        int currentUserId = userDao.findIdByUsername(currentUsername);
        if(!(currentUserId == transfer.getFromUserId())){
            return new ResponseEntity<>("unauthorized transfer",HttpStatus.UNAUTHORIZED);
        }
        boolean transferCreated = transferDao.createTransfer(transfer);
        if(transferCreated){
            return new ResponseEntity<>(transfer, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>("transfer could not be completed", HttpStatus.BAD_REQUEST);
        }
    }



    @RequestMapping(path = "/transfer/{transferId}", method = RequestMethod.GET)
    public Transfer getTransfer(@PathVariable Long transferId) {
        Transfer transfer = transferDao.getTransferById(transferId);
        if (transfer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer not found.");
        } else {
            return transfer;
        }
    }

}
