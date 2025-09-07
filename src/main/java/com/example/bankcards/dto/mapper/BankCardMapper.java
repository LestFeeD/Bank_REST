package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.response.BankCardResponseDto;
import com.example.bankcards.entity.BankCard;

public class BankCardMapper {
    public static BankCardResponseDto toDto(BankCard card) {
        if (card == null) {
            return null;
        }

        BankCardResponseDto dto = new BankCardResponseDto();
        dto.setCardNumber(maskCardNumber(card.getCardNumber()));
        dto.setValidThru(card.getValidThru());
        dto.setBalance(card.getBalance());
        dto.setIdStatusCart(card.getStatusCard().getId());

        return dto;
    }

    private static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "**** **** **** ****";
        }

        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }
}
