package com.example.bankcards.dto.response;

import java.math.BigDecimal;
import java.sql.Date;

public class BankCardResponseDto {
    private String cardNumber;
    private Date validThru;
    private BigDecimal balance;
    private Long idStatusCart;



    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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

    public Long getIdStatusCart() {
        return idStatusCart;
    }

    public void setIdStatusCart(Long idStatusCart) {
        this.idStatusCart = idStatusCart;
    }
}
