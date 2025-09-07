package com.example.bankcards.controller;

import com.example.bankcards.dto.BankCardDto;
import com.example.bankcards.dto.TransferCardDto;
import com.example.bankcards.service.AuthenticationService;
import com.example.bankcards.service.BankCardService;
import com.example.bankcards.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.math.BigDecimal;
import java.sql.Date;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BankCardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BankCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private BankCardService bankCardService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void findAllCard_ShouldReturnOk_WhenUserIsAuthorizedAndFindOneCards() throws Exception {

        when(authenticationService.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(get("/user/cards")
                        .param("page", "0")
                        .param("size", "5")
                        .param("search", "1234")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bankCardService, times(1)).findAllCardsByUser(1L, 0, 5, "1234");
    }

    @Test
    void findAllCardByUser_ShouldReturnOk_WhenUserIsAuthorizedAndFindCardsWithSearchByUser() throws Exception {

        when(authenticationService.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(get("/all-cards")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bankCardService, times(1))
                .findAllCards(0, 5);
    }

    @Test
    void createCard_ShouldReturnCreated_WhenUserIsAuthorizedAndValidData() throws Exception {

        BankCardDto dto = new BankCardDto();
        dto.setValidThru(Date.valueOf("2025-12-31"));
        dto.setIdUser(2L);

        when(authenticationService.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(post("/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(bankCardService, times(1))
                .createCard(any(BankCardDto.class));
    }

    @Test
    void blockCard_ShouldReturnOk_WhenUserIsAuthorized() throws Exception {

        when(authenticationService.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(patch("/card/{cartId}/block", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bankCardService, times(1))
                .blockCard(eq(1L), eq(1L));
    }

    @Test
    void activateCard_ShouldReturnOk_WhenUserIsAuthorized() throws Exception {

        when(authenticationService.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(patch("/card/{cartId}/activate", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bankCardService, times(1))
                .activateCard(eq(1L));
    }

    @Test
    void balanceCard_ShouldReturnOk_WhenUserIsAuthorizedAndGetBalance() throws Exception {

        BigDecimal mockBalance = new BigDecimal("1000.00");

        when(authenticationService.getCurrentUserId()).thenReturn(1L);
        when(bankCardService.getBalanceCard(1L, 1L)).thenReturn(mockBalance);

        mockMvc.perform(get("/card/{cartId}/balance", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mockBalance.toString()));

        verify(bankCardService, times(1))
                .getBalanceCard(eq(1L), eq(1L));
    }

    @Test
    void transferCard_ShouldReturnOk_WhenUserIsAuthorizedAndValidData() throws Exception {

        TransferCardDto dto = new TransferCardDto();
        dto.setFromCardId(1L);
        dto.setToCardId(2L);
        dto.setAmount(new java.math.BigDecimal("100.00"));

        when(authenticationService.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(patch("/card/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(bankCardService, times(1))
                .transferCard(any(TransferCardDto.class), eq(1L));
    }

    @Test
    void transferCard_ShouldReturnUnauthorized_WhenUserIsNull() throws Exception {

        TransferCardDto dto = new TransferCardDto();
        dto.setFromCardId(1L);
        dto.setToCardId(2L);
        dto.setAmount(new java.math.BigDecimal("100.00"));

        when(authenticationService.getCurrentUserId()).thenReturn(null);

        mockMvc.perform(patch("/card/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        verify(bankCardService, never()).transferCard(any(), anyLong());
    }

    @Test
    void deleteCard_ShouldReturnOk_WhenUserIsAuthorized() throws Exception {

        when(authenticationService.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(delete("/card/{cartId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bankCardService, times(1))
                .deleteCard(eq(1L));
    }
}
