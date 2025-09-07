package com.example.bankcards.repository;

import com.example.bankcards.entity.BankCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    Optional<BankCard> findByIdAndUser_Id(Long cardId, Long userId);
    Page<BankCard> findByUser_IdAndCardNumberContainingIgnoreCase(Long userId, String cardNumber, Pageable pageable);
    Page<BankCard> findByUser_Id(Long userId, Pageable pageable);
    boolean existsByCardNumber(String cardNumber);

}
