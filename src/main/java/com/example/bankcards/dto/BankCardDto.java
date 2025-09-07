package com.example.bankcards.dto;


import java.math.BigDecimal;
import java.sql.Date;

public class BankCardDto {
    private Date validThru;
    private BigDecimal balance;
    private Long idUser;

    public BankCardDto() {
    }

    public BankCardDto( Date validThru, BigDecimal balance, Long idUser) {
        this.validThru = validThru;
        this.balance = balance;
        this.idUser = idUser;
    }

    public Date getValidThru() {
        return validThru;
    }

    public void setValidThru(Date validThru) {
        this.validThru = validThru;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }
}
