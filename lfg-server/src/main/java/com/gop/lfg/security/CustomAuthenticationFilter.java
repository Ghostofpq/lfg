package com.gop.lfg.security;

import com.google.common.base.Strings;
import com.gop.lfg.data.models.Token;
import com.gop.lfg.exceptions.CustomExpiredTokenExceptionException;
import com.gop.lfg.services.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationFilter implements Filter {
    public static final String HEADER_TOKEN = "x-token";

    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomAuthenticationFailureHandler failureHandler;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("CustomAuthenticationFilter : INIT");
    }

    @Override
    public void destroy() {
        log.info("CustomAuthenticationFilter : DESTROY");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        log.trace("CustomAuthenticationFilter !");
        HttpServletRequest httpServletRequest = (HttpServletRequest) req;
        HttpServletResponse httpServletResponse = (HttpServletResponse) res;

        // Get the access token in the header
        final String encodedToken = httpServletRequest.getHeader(HEADER_TOKEN);
        log.trace(HEADER_TOKEN + ":"+encodedToken);
        if (!Strings.isNullOrEmpty(encodedToken)) {
            try {
                final Token token = jwtService.decode(encodedToken);
                if (DateTime.now().isAfter(token.getExpiresAt())) {
                    failureHandler.commence(httpServletRequest, httpServletResponse,
                            new CustomExpiredTokenExceptionException("Token Expired"));
                }
                // Create the authentication with this token
                SecurityContextHolder.getContext().setAuthentication(new CustomAuthentication(token));
            } catch (JoseException e) {
                log.trace("JoseException", e);
                failureHandler.commence(httpServletRequest, httpServletResponse,
                        new CustomAuthenticationException("InvalidToken"));
            }
        }
        chain.doFilter(req, res);
    }
}
