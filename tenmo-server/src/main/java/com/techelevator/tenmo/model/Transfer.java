package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    private Long transferId;
    private Long fromUserId;
    private Long toUserId;
    private BigDecimal amount;
    private Boolean approved = true;
    private String status;

    public Long getTransferId(){
        return transferId;
    }
    public Long getFromUserId(){
        return fromUserId;
    }
    public Long getToUserId(){
        return toUserId;
    }
    public BigDecimal getAmount(){
        return amount;
    }
    public boolean getApproved(){
        return approved;
    }
    public void setTransferId(Long transferId){
        this.transferId = transferId;
    }
    public void setFromUserId(Long fromUserId){
        this.fromUserId = fromUserId;
    }
    public void setToUserId(Long toUserId){
        this.toUserId = toUserId;
    }
    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }
    public void setApproved(boolean approved){
        this.approved = approved;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
    public Transfer(){

    }
    public Transfer(Long transferId, Long fromUserId, Long toUserId, BigDecimal amount, boolean approved){
        this.transferId = transferId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.approved = approved;
    }

    public boolean createTransfer(Transfer transfer) {
        return true;
    }
}
