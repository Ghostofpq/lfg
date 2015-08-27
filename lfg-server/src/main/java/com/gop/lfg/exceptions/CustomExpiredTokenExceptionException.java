package com.gop.lfg.exceptions;

import org.springframework.security.core.AuthenticationException;

public class CustomExpiredTokenExceptionException extends AuthenticationException {
    public CustomExpiredTokenExceptionException(String message) {
        super(message);
    }
}
