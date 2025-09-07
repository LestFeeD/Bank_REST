package com.example.bankcards.service;

import com.example.bankcards.dto.BankCardDto;
import com.example.bankcards.dto.TransferCardDto;
import com.example.bankcards.dto.mapper.BankCardMapper;
import com.example.bankcards.dto.response.BankCardResponseDto;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.StatusCardRepository;
import com.example.bankcards.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class BankCardService {
    private final BankCardRepository bankCardRepository;
    private final StatusCardRepository statusCardRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    @Autowired
    public BankCardService(BankCardRepository bankCardRepository, StatusCardRepository statusCardRepository, UserRepository userRepository) {
        this.bankCardRepository = bankCardRepository;
        this.statusCardRepository = statusCardRepository;
        this.userRepository = userRepository;
    }

    public Page<BankCardResponseDto> findAllCards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("cardNumber").ascending());
        Page<BankCard> bankCards = bankCardRepository.findAll(pageable);

        return bankCards.map(BankCardMapper::toDto);
    }


    public Page<BankCardResponseDto> findAllCardsByUser(Long id, int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("cardNumber").ascending());
        Page<BankCard> bankCards = null;
        if (search == null || search.isBlank()) {
            bankCards =  bankCardRepository.findByUser_Id(id, pageable);
        } else {
            bankCards = bankCardRepository.findByUser_IdAndCardNumberContainingIgnoreCase(id, search, pageable);
        }

        if (bankCards == null) {
            bankCards = Page.empty(pageable);
        }
        return bankCards.map(BankCardMapper::toDto);

    }

    public void blockCard(Long userId, Long cardId) {

        BankCard card = bankCardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("The card wasn't found."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("The user wasn't found."));

        if (!card.getUser().getId().equals(userId)) {
            if (user.getRole().getName() == RoleEnum.USER) {
                throw new ForbiddenException("The card doesn't belong to the user.");
            }
        }

        if (card.getStatusCard().getName() == StatusCardEnum.BLOCKED) {
            throw new BadRequestException("The card has already been blocked.");
        }

        StatusCard blockedStatus = statusCardRepository.findByName(StatusCardEnum.BLOCKED);
        card.setStatusCard(blockedStatus);
        bankCardRepository.save(card);

    }

    public void activateCard(Long cardId) {

        BankCard card = bankCardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("The card wasn't found."));

        if (card.getStatusCard().getName() == StatusCardEnum.ACTIVE) {
            throw new BadRequestException("The card is already active.");
        }

        StatusCard blockedStatus = statusCardRepository.findByName(StatusCardEnum.ACTIVE);
        card.setStatusCard(blockedStatus);
        bankCardRepository.save(card);
    }

    public void createCard(BankCardDto requestDto) {
        User user = userRepository.findById(requestDto.getIdUser()).orElseThrow(() -> new NotFoundException("The user wasn't found."));
        StatusCard statusCard = statusCardRepository.findByName(StatusCardEnum.ACTIVE);

        if (requestDto.getValidThru() == null) {
            throw new BadRequestException("The validity period of the card is required.");
        }

        if ( requestDto.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("The balance cannot be negative.");
        }

        BankCard bankCard = BankCard.builder()
                .cardNumber(generateUniqueCardNumber())
                .validThru(requestDto.getValidThru())
                .balance(requestDto.getBalance() != null ? requestDto.getBalance() : BigDecimal.ZERO )
                .statusCard(statusCard)
                .user(user)
                .build();

        bankCardRepository.save(bankCard);


    }

    public void transferCard(TransferCardDto requestDto, Long userId) {

        BankCard fromCard = bankCardRepository.findByIdAndUser_Id(requestDto.getFromCardId(), userId)
                .orElseThrow(() -> new NotFoundException("The card wasn't found."));

        BankCard toCard = bankCardRepository.findById(requestDto.getToCardId())
                .orElseThrow(() -> new NotFoundException("The card wasn't found."));


        if (fromCard.getBalance().compareTo(requestDto.getAmount()) < 0 || fromCard.getStatusCard().getName() != StatusCardEnum.ACTIVE ) {
            throw new IllegalStateException("Insufficient funds or the card is unavailable for transfer.");
        }

        if (toCard.getStatusCard().getName() != StatusCardEnum.ACTIVE) {
            throw new IllegalStateException("Insufficient funds or the card is unavailable for transfer.");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(requestDto.getAmount()));
        toCard.setBalance(toCard.getBalance().add(requestDto.getAmount()));
        bankCardRepository.save(fromCard);
        bankCardRepository.save(toCard);

    }

    public BigDecimal getBalanceCard(Long cardId, Long userId) {

        BankCard card = bankCardRepository.findByIdAndUser_Id(cardId, userId)
                .orElseThrow(() -> new RuntimeException("Card not found or does not belong to user"));

        return card.getBalance();
    }

    public void deleteCard(Long cardId) {
        BankCard card = bankCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found or doesn't belong to user"));

        bankCardRepository.delete(card);
    }

    public String generateUniqueCardNumber() {
        String cardNumber;
        do {
            cardNumber = generateRandom16Digits();
        } while (bankCardRepository.existsByCardNumber(cardNumber));
        return cardNumber;
    }

    private String generateRandom16Digits() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}
