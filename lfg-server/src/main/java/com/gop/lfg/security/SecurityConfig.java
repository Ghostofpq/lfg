package com.gop.lfg.security;

import com.gop.lfg.services.JwtService;
import com.gop.lfg.services.TokenService;
import com.gop.lfg.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private SecurityProperties security;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private JwtService jwtService;
    //@Autowired
    //private CustomAuthenticationFailureHandler failureHandler;
    @Autowired
    private CustomAuthenticationFilter authenticationFilter;

    @PostConstruct
    private void init() {
        log.info("SecurityConfig Loaded");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("Configure");
        http.csrf().disable();
        http.httpBasic().disable();

        // API whitelist
        http.authorizeRequests().antMatchers("/api/user", "/api/token/create", "/api/token/refresh").permitAll();
        // API secured (all except whitelist)
        http.authorizeRequests().antMatchers("/api/**").authenticated();

        //http.exceptionHandling().authenticationEntryPoint(failureHandler);
        http.addFilterBefore(authenticationFilter, AnonymousAuthenticationFilter.class);

    }

    // @Override
    // protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    //     auth.authenticationProvider(new CustomAuthenticationManager(tokenService, userService));
    // }

    // @Bean
    // @Override
    // public AuthenticationManager authenticationManagerBean() throws Exception {
    //     return super.authenticationManagerBean();
    // }
}
