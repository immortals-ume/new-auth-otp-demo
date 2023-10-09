package com.demo.auth.authdemoproject.util;

import com.demo.auth.authdemoproject.service.exception.InvalidEmailException;
import com.demo.auth.authdemoproject.service.exception.InvalidPasswordException;

import java.util.regex.Pattern;

import static com.demo.auth.authdemoproject.constant.AuthConstant.EMAIL_REGEX;
import static com.demo.auth.authdemoproject.constant.AuthConstant.PASSWORD_REGEX;


public class ValidateCredentialsUtils {

    private ValidateCredentialsUtils() {
        throw new IllegalStateException("Validate Credentials Utils Class");
    }

    public static void validatePassword(String password) {
        if (!(Pattern.matches(PASSWORD_REGEX, password) && password.length() < 8)) {
            throw new InvalidPasswordException("Password Does Not match the Requirements, Please Provide a New Password ");
        }
    }

    public static void validateEmail(String email) {
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new InvalidEmailException("Email Does Not match the Requirements, Please Provide a New Email  ");
        }
    }
}
