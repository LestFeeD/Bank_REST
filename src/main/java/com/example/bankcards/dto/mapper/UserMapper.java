package com.example.bankcards.dto.mapper;

import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.User;

public class UserMapper {
    public static UserResponseDto toDto(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDto dto = new UserResponseDto();
        dto.setLastName(user.getLastName());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setPassword(user.getPassword());

        return dto;
    }
}
