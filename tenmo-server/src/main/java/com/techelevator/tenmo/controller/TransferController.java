package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
public class TransferController {

    private final TransferDAO transferDao;
    public TransferController(TransferDAO transferDao){
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public ResponseEntity<?> createTransfer(@RequestBody Transfer transfer, Principal authentication) {
        String currentUsername = authentication.getName();
        boolean transferCreated = transferDao.createTransfer(transfer);
        if(transferCreated){
            return new ResponseEntity<>(transfer, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(transfer, HttpStatus.BAD_REQUEST);
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
