package com.example.bankcards.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "status_card")
public class StatusCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusCardEnum name;

    @OneToMany(mappedBy = "statusCard", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<BankCard> bankCards;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusCardEnum getName() {
        return name;
    }

    public void setName(StatusCardEnum name) {
        this.name = name;
    }

    public Set<BankCard> getBankCards() {
        return bankCards;
    }

    public void setBankCards(Set<BankCard> bankCards) {
        this.bankCards = bankCards;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StatusCard that = (StatusCard) o;
        return Objects.equals(id, that.id) && name == that.name && Objects.equals(bankCards, that.bankCards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, bankCards);
    }
}
