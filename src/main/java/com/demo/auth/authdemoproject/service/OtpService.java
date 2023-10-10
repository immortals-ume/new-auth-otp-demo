package com.demo.auth.authdemoproject.service;

import java.util.List;

public interface OtpService {
    Boolean generateOtp(String key, List<String> recipients);

    Boolean validateOTP(String key, Integer otpNumber);
}
