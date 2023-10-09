package com.demo.auth.authdemoproject.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginInfoDto {

    @JsonProperty("userName")
    private String userName;

    @NotNull
    @Email
    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

}
