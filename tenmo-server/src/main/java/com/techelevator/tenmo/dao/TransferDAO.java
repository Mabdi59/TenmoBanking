package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class TransferDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Transactional
    public boolean createTransfer(Transfer transfer) {
        BigDecimal senderBalance = getBalance(transfer.getFromUserId());
        if (senderBalance.compareTo(transfer.getAmount()) < 0) {
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

    private Transfer mapRowToTransfer() {
        Transfer transfer = new Transfer();
        transfer.setTransferId(transfer.getTransferId());
        transfer.setAmount(transfer.getAmount());
        transfer.setFromUserId(transfer.getFromUserId());
        transfer.setToUserId(transfer.getToUserId());
        transfer.setApproved(true);
        return transfer;
    }
}
