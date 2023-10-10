package com.demo.auth.authdemoproject.service;


import com.demo.auth.authdemoproject.mapper.UserMapper;
import com.demo.auth.authdemoproject.model.dto.LoginInfoDto;
import com.demo.auth.authdemoproject.model.dto.VerifyTokenRequestDTO;
import com.demo.auth.authdemoproject.model.entity.User;
import com.demo.auth.authdemoproject.model.enums.AuthProvider;
import com.demo.auth.authdemoproject.repository.UserRepository;
import com.demo.auth.authdemoproject.security.jwt.JwtProvider;
import com.demo.auth.authdemoproject.service.exception.UserAlreadyExistsException;
import com.demo.auth.authdemoproject.service.exception.UserNameAlreadyExistsException;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.demo.auth.authdemoproject.util.DateUtils.getLocalDateTime;
import static com.demo.auth.authdemoproject.util.ValidateCredentialsUtils.validateEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final JwtProvider jwtProvider;

    private final UserMapper userMapper;

    private final OtpService otpService;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = {Exception.class})
    @Override
    public String register(@NotNull LoginInfoDto loginInfoDto) {
        if (Optional.ofNullable(userRepository.findByUserNameOrEmailAndActiveIndTrue(loginInfoDto.getEmail())).isPresent()) {
            throw new UserAlreadyExistsException("User Already Exists with this Email");
        }

        if (Optional.ofNullable(userRepository.findByUserNameOrEmailAndActiveIndTrue(loginInfoDto.getUserName())).isPresent()) {
            throw new UserAlreadyExistsException("User Already Exists with this UserName");
        }
        validateEmail(loginInfoDto.getEmail());
        User user = userMapper.toUser(loginInfoDto);
        user.setPassword(getPassword(loginInfoDto.getPassword()));
        user.setCreatedBy(1L);
        user.setCreatedAt(getLocalDateTime());
        user.setActiveInd(Boolean.TRUE);
        user.setAuthProvider(AuthProvider.FORM_LOGIN);
        return userRepository.saveAndFlush(user).getUserName();
    }

    private String getPassword(String password) {
       // validatePassword(password);
        return encoder.encode(password);
    }


    @Override
    public Boolean login(@NotNull LoginInfoDto loginInfoDto) {
        Authentication authentication;
        Boolean signedJwt = false;
        List<String>  emails = new ArrayList<>() ;
        if (loginInfoDto.getUserName() != null) {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginInfoDto.getUserName(), loginInfoDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            emails.add(findUser(loginInfoDto.getUserName()).getEmail());
            if (authentication.isAuthenticated()) {
                signedJwt = otpService.generateOtp(loginInfoDto.getUserName(),emails);
            }

        } else {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginInfoDto.getEmail(), loginInfoDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            emails.add(loginInfoDto.getEmail());
            if (authentication.isAuthenticated()) {
                signedJwt = otpService.generateOtp(loginInfoDto.getEmail(),emails);
            }
        }

        return signedJwt;
    }

    @Override
    public Boolean checkUserNameIsAvailable(String userName) {
        boolean existsOrNot = userRepository.existsByUserName(userName);
        if (!existsOrNot) {
            throw new UserNameAlreadyExistsException("User Already Exists With this userName : " + userName);
        }
        return existsOrNot;
    }

    @Override
    public String findEmailByUsername(String userNameOrEmail) {
        return findUser(userNameOrEmail).getEmail();
    }

    @Override
    public String createTokenAfterVerifiedOtp(VerifyTokenRequestDTO verifyTokenRequest) {
        Authentication authentication;
        String signedJwt = "";
        User user = findUser(verifyTokenRequest.getUsername());
        try {
            if (verifyTokenRequest.getUsername() != null) {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);


                signedJwt = jwtProvider.generateJwtToken(authentication);


                updateUserLoginLogout(user.getUserName(), getLocalDateTime(), Boolean.FALSE);
            } else {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                signedJwt = jwtProvider.generateJwtToken(authentication);

                updateUserLoginLogout(user.getEmail(), getLocalDateTime(), Boolean.FALSE);
            }

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | JOSEException e) {
            log.error("Error Occurred While Logging The User {}", e.getLocalizedMessage());
        }
        return signedJwt;
    }

    public User findUser(String userName) {
        return Optional.ofNullable(userRepository.findByUserNameOrEmailAndActiveIndTrue(userName))
                .orElseThrow(() -> new UsernameNotFoundException(" User not found"));
    }

    public void updateUserLoginLogout(String userName, LocalDateTime loginLogOutTime, Boolean flag) {
        User user = findUser(userName);
        if (Boolean.FALSE.equals(flag)) {
            user.setLogin(loginLogOutTime);
        }
        userRepository.saveAndFlush(user);
    }


}
