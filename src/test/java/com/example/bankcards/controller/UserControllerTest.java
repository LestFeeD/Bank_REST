package com.example.bankcards.controller;

import com.example.bankcards.dto.LoginRequestDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.AuthenticationService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class,
        excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    @Qualifier("userService")
    private UserService userService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void findUserById_ShouldReturnOk_WhenUserIsAuthorizedAndReturnUser() throws Exception {

        User mockUser = new User();

        when(authenticationService.getCurrentUserId()).thenReturn(2L);
        when(userService.findUser(2L, 1L)).thenReturn(mockUser);

        mockMvc.perform(get("/user/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .findUser(eq(2L), eq(1L));
    }

    @Test
    void login_ShouldReturnOk_WhenUserIsAuthorizedAndReturnJwt() throws Exception {

        String jwt = "mocked-jwt-token";
        LoginRequestDto requestDto = new LoginRequestDto();

        when(authenticationService.getCurrentUserId()).thenReturn(2L);
        when(userService.loginUser(any(LoginRequestDto.class))).thenReturn(jwt);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("{token=" + jwt + "}"));

        verify(userService, times(1))
                .loginUser(any(LoginRequestDto.class));
    }

    @Test
    void registration_ShouldReturnOk_WhenUserIsAuthorizedAndReturnUserResponseDto() throws Exception {

        UserDto requestDto = new UserDto();
        UserResponseDto user = new UserResponseDto();

        when(authenticationService.getCurrentUserId()).thenReturn(2L);
        when(userService.registration(requestDto)).thenReturn(user);

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .registration(any(UserDto.class));
    }

    @Test
    void editUser_ShouldReturnOk_WhenUserIsAuthorizedAndReturnUserResponseDto() throws Exception {

        UserResponseDto responseDto = new UserResponseDto();
        UserDto requestDto = new UserDto();
        when(authenticationService.getCurrentUserId()).thenReturn(2L);
        when(userService.editUser(requestDto)).thenReturn(responseDto);

        mockMvc.perform(patch("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .editUser(any(UserDto.class));
    }

    @Test
    void blockOrDeleteUser_ShouldReturnOk_WhenUserIsAuthorized() throws Exception {

        when(authenticationService.getCurrentUserId()).thenReturn(2L);
        doNothing().when(userService).blockUser(1L);

        mockMvc.perform(patch("/user/{userId}/block", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .blockUser(1L);
    }
}
