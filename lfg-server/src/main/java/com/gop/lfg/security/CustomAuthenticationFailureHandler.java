package com.gop.lfg.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gop.lfg.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Arthur Viguier (xqmx7112) on 10/03/2015.
 */
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationEntryPoint {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setStatus(403);
        ErrorMessage errorMessage = new ErrorMessage(e.getMessage());
        httpServletResponse.getOutputStream().print(mapper.writeValueAsString(errorMessage));
    }
}

