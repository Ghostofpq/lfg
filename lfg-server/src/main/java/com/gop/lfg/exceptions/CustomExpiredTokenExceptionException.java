package com.gop.lfg.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Mathieu Perez (vmpx4526)
 */
public class CustomExpiredTokenExceptionException extends AuthenticationException {
    public CustomExpiredTokenExceptionException(String message) {
        super(message);
    }
}
