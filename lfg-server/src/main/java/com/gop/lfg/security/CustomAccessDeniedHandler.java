package com.gop.lfg.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gop.lfg.exceptions.CustomExpiredTokenExceptionException;
import com.gop.lfg.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by GhostOfPQ on 14/08/2015.
 */

@Slf4j
@Deprecated
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        final ErrorMessage errorMessage;
        if (httpServletResponse.getHeader("token-expired").equals("true")) {
            httpServletResponse.setStatus(418);
            errorMessage = new ErrorMessage("Token Expired");
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            errorMessage = new ErrorMessage("Token Invalid");
        }
        httpServletResponse.getOutputStream().print(mapper.writeValueAsString(errorMessage));
    }
}
