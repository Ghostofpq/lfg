package com.gop.lfg.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class CustomAuthenticationFilter implements Filter {
    public static final String HEADER_ACCESS_TOKEN = "ACCESS-TOKEN";

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
        HttpServletRequest httpServletRequest = (HttpServletRequest) req;
        // Get the access token in the header
        final String accessToken = httpServletRequest.getHeader(HEADER_ACCESS_TOKEN);
        // Create the authentication with this token
        CustomAuthentication authentication = new CustomAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, res);
    }

}
