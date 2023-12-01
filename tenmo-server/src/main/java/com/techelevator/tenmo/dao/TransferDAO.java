package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransferDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Transactional
    public boolean createTransfer(Transfer transfer) {
        BigDecimal senderBalance = getBalance(transfer.getFromUserId());
        if (senderBalance.compareTo(transfer.getAmount()) < 0) {
            String sql = "INSERT INTO transfer(from_user_id, to_user_id, amount, approved) VALUES(?,?,?,?);";
            jdbcTemplate.update(sql, 0,0,0, false);
            throw new IllegalStateException("insufficient funds for transfer");
        }

        String sql = "INSERT INTO transfer(from_user_id, to_user_id, amount, approved) VALUES(?,?,?,?);";
        jdbcTemplate.update(sql, transfer.getFromUserId(), transfer.getToUserId(), transfer.getAmount(), transfer.getApproved());
        String sqlUpdateSenderBalance = "UPDATE account SET balance = balance - ? WHERE user_id = ?";
        jdbcTemplate.update(sqlUpdateSenderBalance, transfer.getAmount(), transfer.getFromUserId());
        String sqlUpdateReceiverBalance = "UPDATE account SET balance = balance + ? WHERE user_id = ?";
        jdbcTemplate.update(sqlUpdateReceiverBalance, transfer.getAmount(), transfer.getToUserId());
        return true;
    }

    private BigDecimal getBalance(Long userId) {
        String sql = "SELECT balance FROM account WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
    }

    public Transfer getTransferById(Long transferId) {
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferId);

       if(result.next()) {
           return mapRowToTransfer(result);

           }else {
           return null;
       }
    }

    private Transfer mapRowToTransfer(SqlRowSet result) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(transfer.getTransferId());
        transfer.setAmount(transfer.getAmount());
        transfer.setFromUserId(transfer.getFromUserId());
        transfer.setToUserId(transfer.getToUserId());
        transfer.setApproved(transfer.getApproved());
        return transfer;
    }
}
