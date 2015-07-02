package com.gop.lfg.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gop.lfg.exceptions.CustomExpiredTokenExceptionException;
import com.gop.lfg.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Arthur Viguier (xqmx7112) on 10/03/2015.
 */
@Slf4j
@Component("CustomAuthenticationFailureHandler")
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint {
    final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        final  ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        if (e instanceof CustomExpiredTokenExceptionException) {
            httpServletResponse.setStatus(418);
        } else {
            httpServletResponse.setStatus(403);
        }
        httpServletResponse.getOutputStream().print(mapper.writeValueAsString(errorMessage));
    }
}

