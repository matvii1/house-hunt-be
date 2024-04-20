package com.house.hunter.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static BCryptPasswordEncoder getPasswordEncoder() {
        return encoder;
    }

    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

}
