package com.gop.lfg.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.gop.lfg.data.models.EncodedToken;
import com.gop.lfg.data.models.Token;
import com.gop.lfg.data.models.User;
import com.gop.lfg.exceptions.*;
import com.gop.lfg.services.JwtService;
import com.gop.lfg.services.TokenService;
import com.gop.lfg.services.UserService;
import com.gop.lfg.utils.TokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

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
    public EncodedToken create(@RequestBody final TokenRequest tokenRequest) throws CustomNotFoundException, CustomBadRequestException, JoseException, JsonProcessingException {
        User user = userService.getByLogin(tokenRequest.getLogin());
        if (!user.isPassword(tokenRequest.getPassword())) {
            throw new CustomInvalidLoginOrPasswordException("Invalid login or Password");
        }
        Token token = new Token(user);
        token = tokenService.add(token);
        if (Strings.isNullOrEmpty(tokenRequest.getNickname())) {
            tokenRequest.setNickname("default");
        }
        user.getTokens().put(tokenRequest.getNickname(), token.getId());
        userService.update(user);
        // Clean previous tokens
        for (final Token t : tokenService.getByUser(user.getId())) {
            if (!user.getTokens().containsValue(t.getId())) {
                tokenService.delete(t.getId());
                log.debug(t.toString() + " was removed");
            }
        }

        return jwtService.encode(token);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    @ResponseBody
    public EncodedToken refresh(@RequestBody final EncodedToken token) throws Exception {
        final Token decodedToken = jwtService.decode(token.getValue());
        final Token storedToken = tokenService.getByAccessToken(decodedToken.getAccessToken());
        if (!decodedToken.getRefreshToken().equals(storedToken.getRefreshToken())) {
            throw new CustomInvalidLoginOrPasswordException("Invalid refresh");
        }
        User user = userService.get(decodedToken.getId());
        Token refreshedToken = new Token(user);
        refreshedToken = tokenService.update(refreshedToken);

        return jwtService.encode(refreshedToken);
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public Token get() throws CustomNotFoundException {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return tokenService.getByAccessToken((String) authentication.getDetails());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseBody
    private ErrorMessage handleNotFoundException(CustomNotFoundException e) {
        log.error(HttpStatus.NOT_FOUND + ":" + e.getMessage());
        return new ErrorMessage(e.getMessage());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({CustomBadRequestException.class, JoseException.class, JsonProcessingException.class})
    @ResponseBody
    private ErrorMessage handleBadRequestException(CustomBadRequestException e) {
        log.error(HttpStatus.BAD_REQUEST + ":" + e.getMessage());
        return new ErrorMessage(e.getMessage());
    }
}
