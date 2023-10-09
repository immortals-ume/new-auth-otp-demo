package com.demo.auth.authdemoproject.controller;


import com.demo.auth.authdemoproject.model.dto.LoginInfoDto;
import com.demo.auth.authdemoproject.model.entity.UserProfile;
import com.demo.auth.authdemoproject.security.CurrentUser;
import com.demo.auth.authdemoproject.service.AuthService;
import com.demo.auth.authdemoproject.service.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.demo.auth.authdemoproject.constant.AuthConstant.HEADER_STRING;
import static com.demo.auth.authdemoproject.constant.AuthConstant.TOKEN_PREFIX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginInfoDto loginInfoDto) {

        HttpHeaders responseHeaders = new HttpHeaders();


        responseHeaders.set(HEADER_STRING, TOKEN_PREFIX + authService.login(loginInfoDto));

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("Authenticated the User Successfully");
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public String registerUser(@Valid @RequestBody LoginInfoDto loginInfoDto) {
        return authService.register(loginInfoDto);
    }

    @GetMapping(value = "/checkUserNameIsAvailable/{userName}", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Boolean checkUserNameIsAvailable(@NotNull @Valid @PathVariable String userName) {
        return authService.checkUserNameIsAvailable(userName);
    }

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public UserProfile getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return new UserProfile(currentUser.getUsername(),currentUser.getEmail());
    }
}
