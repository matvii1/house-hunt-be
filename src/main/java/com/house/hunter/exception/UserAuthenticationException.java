package com.house.hunter.exception;

import javax.naming.AuthenticationException;

public class UserAuthenticationException extends AuthenticationException {
    public UserAuthenticationException(String message) {
        super(message);
    }
}
