package com.gop.lfg;

import com.google.common.base.Charsets;
import com.gop.lfg.utils.CorsFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableConfigurationProperties
public class LfgApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(LfgApplication.class, args);
    }

    /**
     * Cors Filter
     */
    @Bean
    public FilterRegistrationBean corsFilterRegistrationBean() {

        CorsFilter corsFilter = new CorsFilter();

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(corsFilter);

        Collection<String> urls = new ArrayList<String>();
        urls.add("/*");
        registrationBean.setUrlPatterns(urls);

        return registrationBean;
    }

    /**
     * UTF8 filter
     */
    @Bean
    public FilterRegistrationBean utf8FilterRegistrationBean() {

        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        characterEncodingFilter.setEncoding(Charsets.UTF_8.toString());
        characterEncodingFilter.setForceEncoding(true);
        registrationBean.setFilter(characterEncodingFilter);

        Collection<String> urls = new ArrayList<String>();
        urls.add("/*");
        registrationBean.setUrlPatterns(urls);

        return registrationBean;
    }

    @Bean
    public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {

        ServletRegistrationBean registration = new ServletRegistrationBean(
                dispatcherServlet);
        registration.addUrlMappings("/*");
        return registration;
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

}
