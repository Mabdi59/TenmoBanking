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
        if (!isTransferValid(transfer)) {
            return false;
        }

        String sqlInsertTransfer = "INSERT INTO transfer (from_user_id, to_user_id, amount, status) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsertTransfer, transfer.getFromUserId(), transfer.getToUserId(), transfer.getAmount(), transfer.getStatus());

        if (transfer.getStatus().equals("Approved")) {
            updateBalances(transfer.getFromUserId(), transfer.getToUserId(), transfer.getAmount());
        }

        return true;
    }

    public Transfer getTransferById(Long transferId) {
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if (results.next()) {
            return mapRowToTransfer(results);
        }
        return null;
    }

    private boolean isTransferValid(Transfer transfer) {
        if (transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        if (transfer.getFromUserId().equals(transfer.getToUserId())) {
            return false;
        }

        BigDecimal senderBalance = getBalance(transfer.getFromUserId());
        if (senderBalance.compareTo(transfer.getAmount()) < 0) {
            return false;
        }

        return true;
    }

    private BigDecimal getBalance(Long fromUserId) {
        String sql = "SELECT balance FROM account WHERE user_id = ?";
        try {
            BigDecimal balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, fromUserId);
            return balance != null ? balance : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private void updateBalances(Long fromUserId, Long toUserId, BigDecimal amount) {
        String sqlUpdateSenderBalance = "UPDATE account SET balance = balance - ? WHERE user_id = ?";
        jdbcTemplate.update(sqlUpdateSenderBalance, amount, fromUserId);

        String sqlUpdateReceiverBalance = "UPDATE account SET balance = balance + ? WHERE user_id = ?";
        jdbcTemplate.update(sqlUpdateReceiverBalance, amount, toUserId);
    }

    public boolean approveTransfer(Long transferId) {
        Transfer transfer = getTransferById(transferId);

        String sqlUpdateTransfer = "UPDATE transfer SET status = 'Approved' WHERE transfer_id = ?";
        jdbcTemplate.update(sqlUpdateTransfer, transferId);

        String sqlUpdateBalances = "UPDATE account SET balance = CASE WHEN user_id = ? THEN balance - ? WHEN user_id = ? THEN balance + ? END WHERE user_id IN (?, ?)";
        jdbcTemplate.update(sqlUpdateBalances, transfer.getFromUserId(), transfer.getAmount(), transfer.getToUserId(), transfer.getAmount(), transfer.getFromUserId(), transfer.getToUserId());
        return true;
    }
    public boolean rejectTransfer(Long transferId) {
        String sqlUpdateTransfer = "UPDATE transfer SET status = 'Rejected' WHERE transfer_id = ?";
        int rowsAffected = jdbcTemplate.update(sqlUpdateTransfer, transferId);
        return rowsAffected == 1;
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getLong("transfer_id"));
        transfer.setFromUserId(rs.getLong("from_user_id"));
        transfer.setToUserId(rs.getLong("to_user_id"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        transfer.setStatus(rs.getString("status"));
        return transfer;
    }

    public List<Transfer> getPendingTransfers(Long currentUserId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE (from_user_id = ? OR to_user_id = ?) AND status = 'Pending'";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, currentUserId, currentUserId);
        while (results.next()) {
            transfers.add(mapRowToTransfer(results));
        }

        return transfers;
    }
}
