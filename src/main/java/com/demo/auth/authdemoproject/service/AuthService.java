package com.demo.auth.authdemoproject.service;


import com.demo.auth.authdemoproject.model.dto.LoginInfoDto;
import com.demo.auth.authdemoproject.model.dto.VerifyTokenRequestDTO;
import jakarta.validation.constraints.NotNull;

public interface AuthService {

    String register(@NotNull LoginInfoDto loginInfoDto);

    Boolean login(@NotNull LoginInfoDto loginInfoDto);


    Boolean checkUserNameIsAvailable(String userName);

    String findEmailByUsername(String userNameOrEmail);

    String createTokenAfterVerifiedOtp(VerifyTokenRequestDTO verifyTokenRequest);
}
