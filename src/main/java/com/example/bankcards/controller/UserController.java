package com.example.bankcards.controller;

import com.example.bankcards.dto.LoginRequestDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.AuthenticationService;
import com.example.bankcards.service.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Autowired
    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        Long userAdminId = authenticationService.getCurrentUserId();
        if (userAdminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user =  userService.findUser(userAdminId,userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/registration")
    public ResponseEntity<UserResponseDto> registration(@RequestBody UserDto requestDto) throws SQLException {
        UserResponseDto user = userService.registration(requestDto);
        return ResponseEntity.ok(user);

    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto) throws BadRequestException {
        String jwt = userService.loginUser(requestDto);

        return ResponseEntity.ok(Map.of("token", jwt).toString());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/user")
    public ResponseEntity<UserResponseDto> editUser( @RequestBody UserDto requestDto) {
        Long userId = authenticationService.getCurrentUserId();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserResponseDto user = userService.editUser(requestDto);

        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/user/{userId}/block")
    public ResponseEntity<Void> blockUser(@PathVariable Long userId) {
        Long idUser = authenticationService.getCurrentUserId();

        if (idUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.blockUser(userId);

        return ResponseEntity.ok().build();
    }
}
