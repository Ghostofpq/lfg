package com.gop.lfg.config;

import com.gop.lfg.security.CustomAuthenticationFilter;
import com.gop.lfg.security.CustomAuthenticationManager;
import com.gop.lfg.security.CustomAuthenticationProvider;
import com.gop.lfg.services.TokenService;
import com.gop.lfg.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import javax.annotation.PostConstruct;

/**
 * Created by VMPX4526 on 25/02/2015.
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private SecurityProperties security;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

 //  @Autowired
 //  private CustomAuthenticationProvider customAuthenticationProvider;

    @PostConstruct
    private void init() {
        log.info("SecurityConfig Loaded");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.httpBasic().disable();

        //URL white list
        http.authorizeRequests().antMatchers("/api/user","/api/token/create","/api/token/refresh", "/api-docs/**", "/index.html", "/swagger/**").permitAll();

        //URL intercepted
        http.authorizeRequests().antMatchers("/api/*").permitAll().anyRequest().authenticated();

        //Session
        //http.formLogin().loginProcessingUrl("/session/login").passwordParameter("password").usernameParameter("username")
        //        .failureHandler(new CustomAuthenticationFailureHandler())
        //        .successHandler(new CustomAuthenticationSuccessHandler()).permitAll();

        //Logout
        //http.logout().logoutUrl("/logout").permitAll();

        http.addFilterBefore(new CustomAuthenticationFilter(), AnonymousAuthenticationFilter.class);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new CustomAuthenticationManager(tokenService,userService));
                //customAuthenticationProvider);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
