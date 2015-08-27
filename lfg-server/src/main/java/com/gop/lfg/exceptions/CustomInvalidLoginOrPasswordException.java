package com.gop.lfg.exceptions;

import org.springframework.security.core.AuthenticationException;

public class CustomInvalidLoginOrPasswordException extends AuthenticationException {
    public CustomInvalidLoginOrPasswordException(String message) {
        super(message);
    }
}
