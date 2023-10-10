package com.demo.auth.authdemoproject.security.filters;


import com.demo.auth.authdemoproject.security.exception.JwtNotFoundException;
import com.demo.auth.authdemoproject.security.jwt.JwtProvider;
import com.demo.auth.authdemoproject.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.demo.auth.authdemoproject.constant.AuthConstant.HEADER_STRING;
import static com.demo.auth.authdemoproject.constant.AuthConstant.TOKEN_PREFIX;


@Provider
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private final List<String> excludedMatchers = List.of("/api/v1/auth/register","/api/v1/auth/login","/api/v1/auth/verify");


    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        return excludedMatchers.stream()
                .anyMatch(matcher -> matcher.matches(String.valueOf(request.getRequestURI())));
    }


    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            if (jwtProvider.isSignatureValid(jwt)) {
                String username = jwtProvider.parseJwtGetUserName(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) throws JwtNotFoundException {
        String bearerToken = request.getHeader(HEADER_STRING);
        if (!(StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX))) {
            throw new JwtNotFoundException("Unable to Obtain JwtToken From header");
        }
        return bearerToken.substring(7);
    }
}
