package com.gop.lfg.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Arthur Viguier (xqmx7112) on 12/03/2015.
 */
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("CustomAuthenticationSuccessHandler called. Granting access to UserId: " + authentication.getName());
        }

        httpServletResponse.sendError(200, "Access Granted to UserId: " + authentication.getName());
    }
}
