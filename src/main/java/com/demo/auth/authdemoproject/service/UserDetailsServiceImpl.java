package com.demo.auth.authdemoproject.service;


import com.demo.auth.authdemoproject.model.entity.User;
import com.demo.auth.authdemoproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userNameOrEmail) throws UsernameNotFoundException {
        User user = Optional.ofNullable(userRepository.findByUserNameOrEmailAndActiveIndTrue(userNameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username or email: " + userNameOrEmail));

        return UserPrincipal.create(user);
    }



}