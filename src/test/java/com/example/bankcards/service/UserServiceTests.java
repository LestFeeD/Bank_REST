package com.example.bankcards.service;


import com.example.bankcards.dto.LoginRequestDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.RoleEnum;
import com.example.bankcards.entity.RoleUser;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.repository.RoleUserRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleUserRepository roleUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private JwtUtils jwtUtils;

    @Test
    void findUser_ShouldReturnUser_WhenCalledByAdminAndUserExists() {
        User admin = new User();
        admin.setId(1L);
        RoleUser adminRole = new RoleUser();
        adminRole.setName(RoleEnum.ADMIN);
        admin.setRole(adminRole);

        User targetUser = new User();
        targetUser.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(2L)).thenReturn(Optional.of(targetUser));

        User result = userService.findUser(1L, 2L);

        assertEquals(2L, result.getId());
        verify(userRepository, times(2)).findById(anyLong());
    }

    @Test
    void findUser_ShouldThrowForbiddenException_WhenCalledByNonAdmin() {
        User user = new User();
        user.setId(1L);
        RoleUser userRole = new RoleUser();
        userRole.setName(RoleEnum.USER);
        user.setRole(userRole);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ForbiddenException.class, () -> userService.findUser(1L, 2L));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).findById(2L);
    }

    @Test
    void registration_ShouldReturnUserResponseDto_WhenValidDataProvided() throws SQLException, SQLException {

        UserDto requestDto = UserDto.builder()
                .name("Test")
                .surname("TestSurname")
                .lastName("TestLast")
                .phone("123456789")
                .email("test@gmail.com")
                .password("123456")
                .build();

        RoleUser roleUser = new RoleUser();
        roleUser.setName(RoleEnum.USER);

        when(roleUserRepository.findByName(RoleEnum.USER)).thenReturn(roleUser);
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());

        User savedUser = User.builder()
                .id(1L)
                .name(requestDto.getName())
                .surname(requestDto.getSurname())
                .lastName(requestDto.getLastName())
                .phone(requestDto.getPhone())
                .email(requestDto.getEmail())
                .password("encodedPassword")
                .role(roleUser)
                .isDeleted(0)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto responseDto = userService.registration(requestDto);

        assertNotNull(responseDto);
        assertEquals("Test", responseDto.getName());
        assertEquals("test@gmail.com", responseDto.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
        verify(roleUserRepository, times(1)).findByName(RoleEnum.USER);
        verify(passwordEncoder, times(1)).encode("123456");
    }

    @Test
    void registration_ShouldThrowBadRequestException_WhenEmailIsMissing() {

        UserDto requestDto = UserDto.builder()
                .name("Test")
                .surname("TestSurname")
                .lastName("TestLast")
                .phone("123456789")
                .email("")
                .password("123456")
                .build();

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.registration(requestDto);
        });

        assertEquals("All required fields must be filled in.", exception.getMessage());
    }



    @Test
    void loginUser_ShouldReturnJwtToken_WhenCalledWithValidData()  {

        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setEmail("test@gmail.com");
        requestDto.setPassword("testPassw");

        User webUser = new User();
        webUser.setPassword("testPassw");
        webUser.setEmail("test@gmail.com");
        webUser.setIsDeleted(0);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(webUser));
        when(jwtUtils.generateToken(authentication)).thenReturn("mock-jwt-token");

        String token = userService.loginUser(requestDto);

        assertEquals("mock-jwt-token", token);
        verify(authManager).authenticate(any());
        verify(jwtUtils).generateToken(authentication);
    }

    @Test
    void loginUser_ShouldThrowBadRequestException_WhenEmailOrPasswordIsMissing() {
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setEmail(null);
        requestDto.setPassword("somePassword");

        assertThrows(BadRequestException.class, () -> {
            userService.loginUser(requestDto);
        });

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.loginUser(requestDto);
        });
        assertEquals("Email and password must not be null", exception.getMessage());
    }


    @Test
    void editUser_ShouldUpdateUser_WhenValidDataProvided() {
        User user = User.builder()
                .id(1L)
                .name("OldName")
                .surname("OldSurname")
                .email("oldEmail@gmail.com")
                .password("oldPassword")
                .phone("123456")
                .build();

        UserDto requestDto = UserDto.builder()
                .id(2L)
                .name("NewName")
                .surname("NewSurname")
                .email("newEmail@gmail.com")
                .password("newPassword")
                .phone("987654")
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .name("NewName")
                .surname("NewSurname")
                .email("newEmail@gmail.com")
                .password("encodedPassword")
                .phone("987654")
                .build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponseDto responseDto = userService.editUser(requestDto);

        assertEquals("NewName", responseDto.getName());
        assertEquals("newEmail@gmail.com", responseDto.getEmail());

        verify(userRepository, times(1)).save(user);
    }

}

