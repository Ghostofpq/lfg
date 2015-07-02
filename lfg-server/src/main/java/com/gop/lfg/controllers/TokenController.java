package com.gop.lfg.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.gop.lfg.data.models.Token;
import com.gop.lfg.data.models.User;
import com.gop.lfg.exceptions.CustomInvalidLoginOrPasswordException;
import com.gop.lfg.services.JwtService;
import com.gop.lfg.services.TokenService;
import com.gop.lfg.services.UserService;
import com.gop.lfg.utils.TokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.ByteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.security.Key;

@Slf4j
@Controller
@RequestMapping("/api/token")
@Component("TokenController")
public class TokenController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    private ShaPasswordEncoder shaEncoder;

    @PostConstruct
    private void init() {
        log.info("TokenController started !");
        shaEncoder = new ShaPasswordEncoder(256);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public String create(@RequestBody final TokenRequest tokenRequest) throws Exception {
        User user = userService.getByLogin(tokenRequest.getLogin());
        final String encodedPassword = shaEncoder.encodePassword(tokenRequest.getPassword(), user.getSalt());
        if (!user.getEncodedPassword().equals(encodedPassword)) {
            throw new CustomInvalidLoginOrPasswordException("Invalid login or Password");
        }
        Token token = new Token(user);
        token = tokenService.add(token);
        if (Strings.isNullOrEmpty(tokenRequest.getNickname())) {
            tokenRequest.setNickname("default");
        }
        user.getTokens().put(tokenRequest.getNickname(), token.getId());
        userService.update(user);

        return jwtService.encode(token);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    @ResponseBody
    public String refresh(@RequestBody final String token) throws Exception {
        final Token decodedToken = jwtService.decode(token);
        final Token storedToken = tokenService.getByAccessToken(decodedToken.getAccessToken());
        if (!decodedToken.getTokenRefresh().equals(storedToken.getTokenRefresh())) {
            throw new CustomInvalidLoginOrPasswordException("Invalid refresh");
        }
        User user = userService.get(decodedToken.getId());
        Token refreshedToken = new Token(user);
        refreshedToken = tokenService.update(refreshedToken);

        return jwtService.encode(refreshedToken);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public Token get() throws Exception {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return tokenService.getByAccessToken((String) authentication.getDetails());
    }
}
