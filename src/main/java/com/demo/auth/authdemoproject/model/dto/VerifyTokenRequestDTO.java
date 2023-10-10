package com.demo.auth.authdemoproject.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class VerifyTokenRequestDTO {

    @NotNull
    private String username;

    private String password;

    @NotNull
    private Integer otp;

}
