package com.gop.lfg.security;

import com.google.common.base.Strings;
import com.gop.lfg.data.models.Token;
import com.gop.lfg.exceptions.CustomNotFoundException;
import com.gop.lfg.services.JwtService;
import com.gop.lfg.services.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.lang.JoseException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationFilter implements Filter {
    public static final String HEADER_TOKEN = "x-token";

    @Inject
    private JwtService jwtService;
    //@Inject
    //private CustomAuthenticationFailureHandler failureHandler;
    @Inject
    private TokenService tokenService;

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
        log.trace(HEADER_TOKEN + ":" + encodedToken);
        if (!Strings.isNullOrEmpty(encodedToken)) {
            try {
                Token token = jwtService.decode(encodedToken);
                if (!token.isValid()) {
                    token = tokenService.refreshToken(token.getRefreshToken());
                    httpServletResponse.setHeader(HEADER_TOKEN, jwtService.encode(token).getValue());
                }
                SecurityContextHolder.getContext().setAuthentication(new CustomAuthentication(token));
            } catch (JoseException | CustomNotFoundException e) {
                log.trace("Exception", e);
                httpServletResponse.setHeader(HEADER_TOKEN, "");
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }
        chain.doFilter(req, res);
    }
}
