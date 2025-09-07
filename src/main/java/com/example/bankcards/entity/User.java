package com.example.bankcards.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user_account")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lastName;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String phone;
    private Integer isDeleted;

    @ManyToOne
    @JoinColumn(name = "id_role")
    private RoleUser role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<BankCard> bankCards;

    public User() {
    }

    public User(Long id, String lastName, String name, String surname, String email, String password, String phone, Integer isDeleted, RoleUser role, Set<BankCard> bankCards) {
        this.id = id;
        this.lastName = lastName;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.isDeleted = isDeleted;
        this.role = role;
        this.bankCards = bankCards;
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

    public RoleUser getRole() {
        return role;
    }

    public void setRole(RoleUser role) {
        this.role = role;
    }

    public Set<BankCard> getBankCards() {
        return bankCards;
    }

    public void setBankCards(Set<BankCard> bankCards) {
        this.bankCards = bankCards;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(lastName, user.lastName) && Objects.equals(name, user.name) && Objects.equals(surname, user.surname) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(isDeleted, user.isDeleted) && Objects.equals(role, user.role) && Objects.equals(bankCards, user.bankCards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lastName, name, surname, email, password, isDeleted, role, bankCards);
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long id;
        private String lastName;
        private String name;
        private String surname;
        private String email;
        private String password;
        private String phone;
        private Integer isDeleted;
        private RoleUser role;
        private Set<BankCard> bankCards;

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }
        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }
        public UserBuilder surname(String surname) {
            this.surname = surname;
            return this;
        }
        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }
        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserBuilder isDeleted(Integer isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }
        public UserBuilder role(RoleUser role) {
            this.role = role;
            return this;
        }
        public UserBuilder bankCards(Set<BankCard> bankCards) {
            this.bankCards = bankCards;
            return this;
        }

        public User build() {
            return new User(id,lastName, name, surname,email, password, phone, isDeleted,  role, bankCards);
        }
    }
}
