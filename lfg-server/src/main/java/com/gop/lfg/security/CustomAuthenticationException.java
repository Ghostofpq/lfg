package com.gop.lfg.security;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by VMPX4526 on 17/06/2015.
 */
public class CustomAuthenticationException extends AuthenticationException {

    public CustomAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public CustomAuthenticationException(String msg) {
        super(msg);
    }
}
