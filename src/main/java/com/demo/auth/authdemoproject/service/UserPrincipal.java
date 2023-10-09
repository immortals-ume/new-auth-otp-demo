package com.demo.auth.authdemoproject.service;


import com.demo.auth.authdemoproject.model.entity.Role;
import com.demo.auth.authdemoproject.model.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserPrincipal implements UserDetails {

    private final Long userId;

    private final String userName;
    @Getter
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    @JsonIgnore
    private final String password;

    public UserPrincipal(Long userId, String userName, String password, String email, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
    }


    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities =  getAuthority(user.getRoles());

        return new UserPrincipal(
                user.getUserId(),
                user.getUserName(),
                user.getPassword(),
                user.getEmail(), authorities);
    }

    private static List<GrantedAuthority> getAuthority(Collection<Role> roles) {      List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role: roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName().name()));
            authorities.addAll(role.getPrivileges()
                    .stream()
                    .map(p -> new SimpleGrantedAuthority(p.getName()))
                    .toList());
        }
        return authorities;

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
        return userName;
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
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal userPrincipal = (UserPrincipal) o;
        return Objects.equals(userId, userPrincipal.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId);
    }


}
