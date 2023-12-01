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
import java.util.List;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferDAO transferDao;
    private final UserDao userDao;

    @Autowired
    public TransferController(TransferDAO transferDao, UserDao userDao) {
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    @PostMapping
    public ResponseEntity<?> createTransfer(@RequestBody Transfer transfer, Principal principal) {
        Long currentUserId = (long) userDao.findIdByUsername(principal.getName());
        if (currentUserId == null || !currentUserId.equals(transfer.getFromUserId())) {
            return new ResponseEntity<>("Unauthorized transfer", HttpStatus.UNAUTHORIZED);
        }

        boolean transferCreated = transferDao.createTransfer(transfer);
        if (transferCreated) {
            return new ResponseEntity<>(transfer, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Transfer could not be completed", HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/{transferId}")
    public ResponseEntity<Transfer> getTransfer(@PathVariable Long transferId) {
        Transfer transfer = transferDao.getTransferById(transferId);
        if (transfer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }
    @GetMapping("/pending")
    public ResponseEntity<List<Transfer>> getPendingTransfers(Principal principal) {
        Long currentUserId = (long) userDao.findIdByUsername(principal.getName());
        List<Transfer> pendingTransfers = transferDao.getPendingTransfers(currentUserId);
        return new ResponseEntity<>(pendingTransfers, HttpStatus.OK);
    }

    @PostMapping("/{transferId}/approve")
    public ResponseEntity<?> approveTransfer(@PathVariable Long transferId, Principal principal) {
        Long currentUserId = (long) userDao.findIdByUsername(principal.getName());

        Transfer transfer = transferDao.getTransferById(transferId);
        if (transfer == null) {
            return new ResponseEntity<>("Transfer not found", HttpStatus.NOT_FOUND);
        }
        // This makes sure logged-in user is the recipient of the transfer
        if (!transfer.getToUserId().equals(currentUserId)) {
            return new ResponseEntity<>("Unauthorized for approval", HttpStatus.UNAUTHORIZED);
        }
        //This Check if the transfer is already processed
        if (!"Pending".equals(transfer.getStatus())) {
            return new ResponseEntity<>("Transfer is not in pending state", HttpStatus.BAD_REQUEST);
        }
        //This is method Reject the transfer
        boolean success = transferDao.approveTransfer(transferId);
        if (success) {
            return new ResponseEntity<>("Transfer approved successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unable to approve transfer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/{transferId}/reject")
    public ResponseEntity<?> rejectTransfer(@PathVariable Long transferId, Principal principal) {
        Long currentUserId = (long) userDao.findIdByUsername(principal.getName());

        Transfer transfer = transferDao.getTransferById(transferId);
        if (transfer == null) {
            return new ResponseEntity<>("Transfer not found", HttpStatus.NOT_FOUND);
        }
        // This makes sure current user matches the transfer's designated recipient
        if (!transfer.getToUserId().equals(currentUserId)) {
            return new ResponseEntity<>("Unauthorized to reject this transfer", HttpStatus.UNAUTHORIZED);
        }
        // This makes sure if the transfer has been previously finalized or is still pending
        if (!"Pending".equals(transfer.getStatus())) {
            return new ResponseEntity<>("Transfer is not in a pending state", HttpStatus.BAD_REQUEST);
        }
        boolean success = transferDao.rejectTransfer(transferId);
        //This confirms and finalize the transfer
        if (success) {
            return new ResponseEntity<>("Transfer rejected successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unable to reject transfer", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}