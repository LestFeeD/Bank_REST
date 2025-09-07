package com.example.bankcards.dto;

import com.example.bankcards.entity.RoleUser;

import java.util.Objects;

public class UserDto {

    private Long id;
    private String lastName;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String phone;
    private Integer isDeleted;
    private RoleUser roleUser;

    public UserDto() {
    }

    public UserDto(Long id, String lastName, String name, String surname, String email, String password, String phone, Integer isDeleted, RoleUser roleUser) {
        this.id = id;
        this.lastName = lastName;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.isDeleted = isDeleted;
        this.roleUser = roleUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public RoleUser getRoleUser() {
        return roleUser;
    }

    public void setRoleUser(RoleUser roleUser) {
        this.roleUser = roleUser;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id) && Objects.equals(lastName, userDto.lastName) && Objects.equals(name, userDto.name) && Objects.equals(surname, userDto.surname) && Objects.equals(email, userDto.email) && Objects.equals(password, userDto.password) && Objects.equals(phone, userDto.phone) && Objects.equals(isDeleted, userDto.isDeleted) && Objects.equals(roleUser, userDto.roleUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastName, name, surname, email, password, phone, isDeleted, roleUser);
    }

    public static UserDtoBuilder builder() {
        return new UserDtoBuilder();
    }

    public static class UserDtoBuilder {
        private Long id;
        private String lastName;
        private String name;
        private String surname;
        private String email;
        private String password;
        private String phone;
        private Integer isDeleted;
        private RoleUser roleUser;

        public UserDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserDtoBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserDtoBuilder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public UserDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserDtoBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserDtoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserDtoBuilder isDeleted(Integer isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public UserDtoBuilder roleUser(RoleUser roleUser) {
            this.roleUser = roleUser;
            return this;
        }


        public UserDto build() {
            return new UserDto(id, lastName, name, surname, email, password, phone, isDeleted, roleUser);
        }
    }
}
