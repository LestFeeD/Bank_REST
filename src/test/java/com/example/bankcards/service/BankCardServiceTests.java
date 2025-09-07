package com.example.bankcards.service;

import com.example.bankcards.dto.BankCardDto;
import com.example.bankcards.dto.TransferCardDto;
import com.example.bankcards.dto.response.BankCardResponseDto;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.repository.BankCardRepository;
import com.example.bankcards.repository.StatusCardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankCardServiceTests {

    @InjectMocks
    private BankCardService bankCardService;

    @Mock
    private BankCardRepository bankCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StatusCardRepository statusCardRepository;


    @Test
    void findAllCards_ShouldReturnPage_WhenCalled()  {

        StatusCard status = new StatusCard();
        status.setId(1L);

        BankCard card1 = new BankCard();
        card1.setStatusCard(status);

        BankCard card2 = new BankCard();
        card2.setStatusCard(status);

        List<BankCard> cards = List.of(card1, card2);
        Page<BankCard> expectedPage = new PageImpl<>(cards);

        when(bankCardRepository.findAll(any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<BankCardResponseDto> result = bankCardService.findAllCards(1, 5);

        assertThat(result.getContent()).hasSize(2);
        verify(bankCardRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void findAllCardsByUser_ShouldReturnPage_WhenCalledWithSearchNull() {

        StatusCard statusCard = new StatusCard();
        statusCard.setId(1L);

        BankCard card1 = new BankCard();
        card1.setCardNumber("1111222233334444");
        card1.setStatusCard(statusCard);

        BankCard card2 = new BankCard();
        card2.setCardNumber("5555666677778888");
        card2.setStatusCard(statusCard);

        List<BankCard> cards = List.of(card1, card2);
        Page<BankCard> expectedPage = new PageImpl<>(cards);

        when(bankCardRepository.findByUser_Id(eq(1L), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<BankCardResponseDto> result = bankCardService.findAllCardsByUser(1L, 0, 5, null);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getCardNumber()).isEqualTo("**** **** **** 4444");
        verify(bankCardRepository, times(1))
                .findByUser_Id(eq(1L), any(Pageable.class));
    }


    @Test
    void findAllCardsByUser_ShouldReturnPage_WhenCalledWithSearch() {

        StatusCard statusCard = new StatusCard();
        statusCard.setId(1L);

        BankCard card1 = new BankCard();
        card1.setCardNumber("1111222233334444");
        card1.setStatusCard(statusCard);

        BankCard card2 = new BankCard();
        card2.setCardNumber("5555666677778888");
        card2.setStatusCard(statusCard);

        List<BankCard> cards = List.of(card1, card2);
        Page<BankCard> expectedPage = new PageImpl<>(cards);

        when(bankCardRepository.findByUser_IdAndCardNumberContainingIgnoreCase(eq(1L),eq("5555"), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<BankCardResponseDto> result = bankCardService.findAllCardsByUser(1L, 0, 5, "5555");

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getCardNumber()).isEqualTo("**** **** **** 4444");
        verify(bankCardRepository, times(1))
                .findByUser_IdAndCardNumberContainingIgnoreCase(eq(1L), eq("5555"), any(Pageable.class));
    }

    @Test
    void blockCard_ShouldSetBlockedStatus_WhenCalledByAdminAndCardActive() {

        StatusCard activeStatus = new StatusCard();
        activeStatus.setName(StatusCardEnum.ACTIVE);

        BankCard card = new BankCard();
        card.setStatusCard(activeStatus);

        RoleUser role = new RoleUser();
        role.setName(RoleEnum.ADMIN);

        User user = new User();
        user.setId(1L);
        user.setRole(role);

        card.setUser(user);

        StatusCard blockedStatus = new StatusCard();
        blockedStatus.setName(StatusCardEnum.BLOCKED);

        when(bankCardRepository.findById(eq(1L))).thenReturn(Optional.of(card));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        when(statusCardRepository.findByName(eq(StatusCardEnum.BLOCKED))).thenReturn(blockedStatus);

        bankCardService.blockCard(1L, 1L);

        assertThat(card.getStatusCard().getName()).isEqualTo(StatusCardEnum.BLOCKED);
        verify(bankCardRepository, times(1)).save(card);
    }

    @Test
    void blockCard_ShouldReturnBadRequestException_WhenCalledByAdminAndCardBlocked() {
        StatusCard blockedStatus = new StatusCard();
        blockedStatus.setName(StatusCardEnum.BLOCKED);

        BankCard card = new BankCard();
        card.setStatusCard(blockedStatus);

        RoleUser role = new RoleUser();
        role.setName(RoleEnum.ADMIN);

        User user = new User();
        user.setId(1L);
        user.setRole(role);

        card.setUser(user);

        when(bankCardRepository.findById(eq(1L))).thenReturn(Optional.of(card));
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> bankCardService.blockCard(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("The card has already been blocked.");

        verify(bankCardRepository, never()).save(any());
    }


    @Test
    void blockCard_ShouldThrowForbiddenException_WhenCalledByAnotherUser() {

        User cardOwner = new User();
        cardOwner.setId(1L);

        StatusCard activeStatus = new StatusCard();
        activeStatus.setName(StatusCardEnum.ACTIVE);

        BankCard card = new BankCard();
        card.setStatusCard(activeStatus);

        RoleUser role = new RoleUser();
        role.setName(RoleEnum.USER);

        User user = new User();
        user.setId(1L);
        user.setRole(role);

        card.setUser(cardOwner);

        when(bankCardRepository.findById(eq(1L))).thenReturn(Optional.of(card));
        when(userRepository.findById(eq(2L))).thenReturn(Optional.of(user));

        assertThrows(ForbiddenException.class, () -> {
        bankCardService.blockCard(2L, 1L);});

        verify(bankCardRepository, never()).save(card);
    }

    @Test
    void activateCard_ShouldSetActiveStatus_WhenCalledByAdminAndCardBlocked() {

        StatusCard blockedStatus = new StatusCard();
        blockedStatus.setName(StatusCardEnum.BLOCKED);

        BankCard card = new BankCard();
        card.setStatusCard(blockedStatus);

        RoleUser role = new RoleUser();
        role.setName(RoleEnum.ADMIN);

        User user = new User();
        user.setId(1L);
        user.setRole(role);

        card.setUser(user);

        StatusCard activeStatus = new StatusCard();
        activeStatus.setName(StatusCardEnum.ACTIVE);

        when(bankCardRepository.findById(eq(1L))).thenReturn(Optional.of(card));
        when(statusCardRepository.findByName(eq(StatusCardEnum.ACTIVE))).thenReturn(activeStatus);

        bankCardService.activateCard(1L);

        assertThat(card.getStatusCard().getName()).isEqualTo(StatusCardEnum.ACTIVE);
        verify(bankCardRepository, times(1)).save(card);
    }

    @Test
    void activateCard_ShouldReturnBadRequestException_WhenCalledByAdminAndCardActive() {

        StatusCard activeStatus = new StatusCard();
        activeStatus.setName(StatusCardEnum.ACTIVE);

        BankCard card = new BankCard();
        card.setStatusCard(activeStatus);

        RoleUser role = new RoleUser();
        role.setName(RoleEnum.ADMIN);

        User user = new User();
        user.setId(1L);
        user.setRole(role);

        card.setUser(user);

        when(bankCardRepository.findById(eq(1L))).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> bankCardService.activateCard(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("The card is already active.");

        verify(bankCardRepository, times(0)).save(any());
    }

    @Test
    void createCard_ShouldReturnId_WhenDataIsValid() {

        User user = new User();
        user.setId(1L);

        StatusCard statusCard = new StatusCard();
        statusCard.setName(StatusCardEnum.ACTIVE);

        BankCardDto requestDto = new BankCardDto();
        requestDto.setIdUser(1L);
        requestDto.setBalance(BigDecimal.valueOf(100));
        requestDto.setValidThru(Date.valueOf(LocalDate.of(2030, 12, 31)));

        BankCard savedCard = new BankCard();
        savedCard.setId(10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(statusCardRepository.findByName(StatusCardEnum.ACTIVE)).thenReturn(statusCard);
        when(bankCardRepository.save(any(BankCard.class))).thenReturn(savedCard);

        bankCardService.createCard(requestDto);

        verify(bankCardRepository).save(any(BankCard.class));
    }

    @Test
    void createCard_ShouldThrowBadRequest_WhenBalanceNegative() {
        BankCardDto requestDto = new BankCardDto();
        requestDto.setIdUser(1L);
        requestDto.setBalance(BigDecimal.valueOf(-5));
        requestDto.setValidThru(Date.valueOf(LocalDate.of(2030, 12, 31)));

        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(statusCardRepository.findByName(StatusCardEnum.ACTIVE)).thenReturn(new StatusCard());

        assertThrows(BadRequestException.class, () -> bankCardService.createCard(requestDto));
    }


    @Test
    void transferCard_ShouldTransferMoney_WhenCardsAreActiveAndEnoughBalance() {

        BankCard fromCard = new BankCard();
        fromCard.setId(1L);
        fromCard.setBalance(BigDecimal.valueOf(100));
        StatusCard activeStatus = new StatusCard();
        activeStatus.setName(StatusCardEnum.ACTIVE);
        fromCard.setStatusCard(activeStatus);

        BankCard toCard = new BankCard();
        toCard.setId(2L);
        toCard.setBalance(BigDecimal.valueOf(50));
        toCard.setStatusCard(activeStatus);

        TransferCardDto requestDto = new TransferCardDto();
        requestDto.setFromCardId(1L);
        requestDto.setToCardId(2L);
        requestDto.setAmount(BigDecimal.valueOf(30));

        when(bankCardRepository.findByIdAndUser_Id(1L, 10L)).thenReturn(Optional.of(fromCard));
        when(bankCardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        bankCardService.transferCard(requestDto, 10L);

        assertEquals(BigDecimal.valueOf(70), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(80), toCard.getBalance());
    }

    @Test
    void transferCard_ShouldThrowIllegalStateException_WhenInsufficientBalance() {
        BankCard fromCard = new BankCard();
        fromCard.setId(1L);
        fromCard.setBalance(BigDecimal.valueOf(8));

        StatusCard activeStatus = new StatusCard();
        activeStatus.setName(StatusCardEnum.ACTIVE);
        fromCard.setStatusCard(activeStatus);

        BankCard toCard = new BankCard();
        toCard.setId(2L);
        toCard.setBalance(BigDecimal.valueOf(50));
        toCard.setStatusCard(activeStatus);

        TransferCardDto requestDto = new TransferCardDto();
        requestDto.setFromCardId(1L);
        requestDto.setToCardId(2L);
        requestDto.setAmount(BigDecimal.valueOf(50));

        when(bankCardRepository.findByIdAndUser_Id(1L, 10L)).thenReturn(Optional.of(fromCard));
        when(bankCardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(IllegalStateException.class, () -> bankCardService.transferCard(requestDto, 10L));
    }

    @Test
    void transferCard_ShouldThrowIllegalStateException_WhenToCardNotActive() {
        BankCard fromCard = new BankCard();
        fromCard.setId(1L);
        fromCard.setBalance(BigDecimal.valueOf(100));
        StatusCard activeStatus = new StatusCard();
        activeStatus.setName(StatusCardEnum.ACTIVE);
        fromCard.setStatusCard(activeStatus);

        BankCard toCard = new BankCard();
        toCard.setId(2L);
        toCard.setBalance(BigDecimal.valueOf(50));
        StatusCard blockedStatus = new StatusCard();
        blockedStatus.setName(StatusCardEnum.BLOCKED);
        toCard.setStatusCard(blockedStatus);

        TransferCardDto requestDto = new TransferCardDto();
        requestDto.setFromCardId(1L);
        requestDto.setToCardId(2L);
        requestDto.setAmount(BigDecimal.valueOf(30));

        when(bankCardRepository.findByIdAndUser_Id(1L, 10L)).thenReturn(Optional.of(fromCard));
        when(bankCardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(IllegalStateException.class, () -> bankCardService.transferCard(requestDto, 10L));
    }

    @Test
    void getBalanceCard_ShouldReturnBalance_WhenCardExists() {

        BankCard card = new BankCard();
        card.setId(1L);
        card.setBalance(BigDecimal.valueOf(150));

        when(bankCardRepository.findByIdAndUser_Id(1L, 10L)).thenReturn(Optional.of(card));

        BigDecimal balance = bankCardService.getBalanceCard(1L, 10L);

        assertEquals(BigDecimal.valueOf(150), balance);
        verify(bankCardRepository, times(1)).findByIdAndUser_Id(1L, 10L);
    }

    @Test
    void deleteCard_ShouldDeleteCard_WhenCardExists() {

        BankCard card = new BankCard();
        card.setId(1L);

        when(bankCardRepository.findById(1L)).thenReturn(Optional.of(card));

        bankCardService.deleteCard(1L);

        verify(bankCardRepository, times(1)).findById(1L);
        verify(bankCardRepository, times(1)).delete(card);
    }

}
