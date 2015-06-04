package com.gop.lfg.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Mathieu Perez (vmpx4526)
 */
public class CustomInvalidLoginOrPasswordException extends AuthenticationException {
    public CustomInvalidLoginOrPasswordException(String message) {
        super(message);
    }
}
