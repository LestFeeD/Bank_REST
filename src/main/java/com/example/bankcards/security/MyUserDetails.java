package com.example.bankcards.security;

import com.example.bankcards.entity.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class MyUserDetails implements UserDetails {

    private Long idUser;
    private String email;
    private String password;
    private Integer isDeleted;
    private Collection<GrantedAuthority> authorities;

    public MyUserDetails(Long idUser, String email, String password, Integer isDeleted, Collection<GrantedAuthority> authorities) {
        this.idUser = idUser;
        this.email = email;
        this.password = password;
        this.isDeleted = isDeleted;
        this.authorities = authorities;
    }

    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public static MyUserDetails buildUserDetails(User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name());
        List<GrantedAuthority> authorities = Collections.singletonList(authority);

        return new MyUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getIsDeleted(),
                authorities);

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isDeleted == 0;
    }
}
