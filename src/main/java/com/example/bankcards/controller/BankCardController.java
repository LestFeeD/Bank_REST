package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardDto;
import com.example.bankcards.dto.TransferCardDto;
import com.example.bankcards.dto.response.BankCardResponseDto;
import com.example.bankcards.service.AuthenticationService;
import com.example.bankcards.service.BankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;

@RestController
public class BankCardController {
    private final BankCardService bankCardService;
    private final AuthenticationService authenticationService;

    @Autowired
    public BankCardController(BankCardService bankCardService, AuthenticationService authenticationService) {
        this.bankCardService = bankCardService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all-cards")
    public ResponseEntity<Page<BankCardResponseDto>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Long idUser = authenticationService.getCurrentUserId();
        if (idUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Page<BankCardResponseDto> cards = bankCardService.findAllCards( page, size);
        return ResponseEntity.ok(cards);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/cards")
    public ResponseEntity<Page<BankCardResponseDto>> getAllCardsByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search) {

        Long userId = authenticationService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Page<BankCardResponseDto> cards = bankCardService.findAllCardsByUser(userId, page, size, search);
        return ResponseEntity.ok(cards);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/card")
    public ResponseEntity<Void> createCard(@RequestBody BankCardDto requestDto) {
        Long idUser = authenticationService.getCurrentUserId();
        if (idUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        bankCardService.createCard(requestDto);
        return ResponseEntity.status(201).build();
    }

    @PatchMapping("/card/{cartId}/block")
    public ResponseEntity<Void> blockCard(@PathVariable Long cartId) {
        Long idUser = authenticationService.getCurrentUserId();
        if (idUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        bankCardService.blockCard(idUser, cartId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/card/{cartId}/activate")
    public ResponseEntity<Void> activateCard(@PathVariable Long cartId) {
        Long idUser = authenticationService.getCurrentUserId();
        if (idUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        bankCardService.activateCard(cartId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/card/{cartId}/balance")
    public ResponseEntity<BigDecimal> balanceCard(@PathVariable Long cartId) {
        Long userId = authenticationService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        BigDecimal balance = bankCardService.getBalanceCard(cartId, userId);
        return ResponseEntity.ok(balance);
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/card/transfer")
    public ResponseEntity<Void> transferCard( @RequestBody TransferCardDto requestDto) {
        Long userId = authenticationService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        bankCardService.transferCard(requestDto, userId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/card/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        Long idUser = authenticationService.getCurrentUserId();
        if (idUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        bankCardService.deleteCard(cardId);
        return ResponseEntity.ok().build();
    }

}
