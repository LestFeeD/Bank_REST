package com.example.bankcards.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "bank_card")
public class BankCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;

    private Date validThru;

    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_status_card")
    @JsonIgnore
    private StatusCard statusCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    @JsonIgnore
    private User user;

    public BankCard() {
    }

    public BankCard(Long id, String cardNumber, Date validThru, BigDecimal balance, StatusCard statusCard, User user) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.validThru = validThru;
        this.balance = balance;
        this.statusCard = statusCard;
        this.user = user;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public StatusCard getStatusCard() {
        return statusCard;
    }

    public void setStatusCard(StatusCard statusCard) {
        this.statusCard = statusCard;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BankCard bankCard = (BankCard) o;
        return Objects.equals(id, bankCard.id) && Objects.equals(cardNumber, bankCard.cardNumber) && Objects.equals(validThru, bankCard.validThru) && Objects.equals(balance, bankCard.balance) && Objects.equals(statusCard, bankCard.statusCard) && Objects.equals(user, bankCard.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cardNumber, validThru, balance, statusCard, user);
    }

    public static BankCardBuilder builder() {
        return new BankCardBuilder();
    }

    public static class BankCardBuilder {
        private Long id;
        private String cardNumber;
        private Date validThru;
        private BigDecimal balance;
        private StatusCard statusCard;
        private User user;

        public BankCardBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BankCardBuilder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public BankCardBuilder validThru(Date validThru) {
            this.validThru = validThru;
            return this;
        }

        public BankCardBuilder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public BankCardBuilder statusCard(StatusCard statusCard) {
            this.statusCard = statusCard;
            return this;
        }

        public BankCardBuilder user(User user) {
            this.user = user;
            return this;
        }

        public BankCard build() {
            return new BankCard(id, cardNumber, validThru, balance, statusCard, user);
        }
    }
}
