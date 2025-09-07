package com.example.bankcards.service;

import com.example.bankcards.dto.LoginRequestDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.mapper.UserMapper;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.RoleEnum;
import com.example.bankcards.entity.RoleUser;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.ForbiddenException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.UnauthorizedException;
import com.example.bankcards.repository.RoleUserRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.MyUserDetails;
import com.example.bankcards.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleUserRepository roleUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    AuthenticationManager authManager;

    @Autowired
    public UserService(UserRepository userRepository, RoleUserRepository roleUserRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleUserRepository = roleUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", email)
        ));

        return MyUserDetails.buildUserDetails(user);
    }

    public User findUser(Long userAdminId, Long userId) {
       User user = userRepository.findById(userAdminId).orElseThrow(() -> new NotFoundException("The user wasn't found."));
        if(!user.getRole().getName().name().equals(RoleEnum.ADMIN.name())) {
            throw new ForbiddenException("Insufficient access rights.");
        }
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("The user wasn't found."));
    }

    @Transactional
    public UserResponseDto registration(UserDto requestDto) {

        if (requestDto.getName() == null || requestDto.getName().isBlank() ||
                requestDto.getLastName() == null || requestDto.getLastName().isBlank() ||
                requestDto.getPhone() == null || requestDto.getPhone().isBlank() ||
                requestDto.getEmail() == null || requestDto.getEmail().isBlank() ||
                requestDto.getPassword() == null || requestDto.getPassword().isBlank()) {
            throw new BadRequestException("All required fields must be filled in.");
        }

        if (!requestDto.getEmail().matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$")) {
            throw new BadRequestException("The email has an incorrect format.");
        }

        if(userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new BadRequestException("The user with this email already exists.");
        }
        if(requestDto.getPassword().length() < 6) {
            throw new BadRequestException("password must be more than 6 characters.");
        }
        RoleUser role = roleUserRepository.findByName(RoleEnum.USER);
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = User.builder()
                .name(requestDto.getName())
                .surname(requestDto.getSurname())
                .lastName(requestDto.getLastName())
                .phone(requestDto.getPhone())
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .role(role)
                .isDeleted(0)
                .build();

       user =  userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Transactional
    public String loginUser(LoginRequestDto requestDto)  {
        if (requestDto.getEmail() == null || requestDto.getPassword() == null) {
            throw new BadRequestException("Email and password must not be null");
        }

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword()));
            User user =  userRepository.findByEmail(requestDto.getEmail()).orElseThrow();

            if (authentication.isAuthenticated() && user.getIsDeleted() == 0) {
                return jwtUtils.generateToken(authentication);
            } else {
                throw new UnauthorizedException("User is not registered");
            }
        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    @Transactional
    public UserResponseDto editUser(UserDto requestDto) {

        User user = userRepository.findById(requestDto.getId()).orElseThrow();

        if (requestDto.getName() != null && !requestDto.getName().isBlank()) {
            user.setName(requestDto.getName());
        }

        if (requestDto.getLastName() != null && !requestDto.getLastName().isBlank()) {
            user.setName(requestDto.getLastName());
        }

        if (requestDto.getSurname() != null && !requestDto.getSurname().isBlank()) {
            user.setSurname(requestDto.getSurname());
        }

        if (requestDto.getEmail() != null && !requestDto.getEmail().isBlank()) {
            user.setEmail(requestDto.getEmail());
        }

        if (requestDto.getPassword() != null && !requestDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        if (requestDto.getPhone() != null && !requestDto.getPhone().isBlank()) {
            user.setPhone(requestDto.getPhone());
        }

       user = userRepository.save(user);

        return UserMapper.toDto(user);
    }

    public void blockUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if(user.getIsDeleted() == 1) {
            throw new BadRequestException("The user has already been blocked.");
        }
        user.setIsDeleted(1);
        userRepository.save(user);
    }
}
