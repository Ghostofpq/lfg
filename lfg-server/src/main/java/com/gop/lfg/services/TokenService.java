package com.gop.lfg.services;

import com.gop.lfg.data.models.Token;
import com.gop.lfg.data.repositories.TokenRepository;
import com.gop.lfg.exceptions.CustomBadRequestException;
import com.gop.lfg.exceptions.CustomNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component("TokenService")
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    @PostConstruct
    private void init() {
        log.info("TokenService started !");
    }

    public Token add(final Token token) throws CustomBadRequestException {
        log.trace("add : {}", token);
        try {
            return tokenRepository.save(token);
        } catch (Exception e) {
            throw new CustomBadRequestException("Could not save new token.");
        }
    }

    public Token get(final String id) throws CustomNotFoundException {
        log.trace("get : {}", id);
        final Token token = tokenRepository.findOne(id);
        if (token != null) {
            return token;
        }
        throw new CustomNotFoundException(id);
    }

    public Token getByAccessToken(final String accessToken) throws CustomNotFoundException {
        log.trace("getByAccessToken : {}", accessToken);
        final Token token = tokenRepository.findByAccessToken(accessToken);
        if (token != null) {
            return token;
        }
        throw new CustomNotFoundException(accessToken);
    }

    public List<Token> getByUser(final String userId) throws CustomNotFoundException {
        log.trace("getByUser : {}", userId);
        return tokenRepository.findByUserId(userId);
    }

    public Token refreshToken(final String refreshToken) throws CustomNotFoundException {
        log.trace("refresh : {}", refreshToken);
        final Token token = tokenRepository.findByRefreshToken(refreshToken);
        if (token != null) {
            token.refresh();
            return tokenRepository.save(token);
        }
        throw new CustomNotFoundException(refreshToken);
    }

    public Token update(final Token token) throws CustomBadRequestException {
        log.trace("update : {}", token);
        try {
            return tokenRepository.save(token);
        } catch (Exception e) {
            throw new CustomBadRequestException("Could not save token.");
        }
    }

    public void delete(final String id) throws CustomNotFoundException {
        log.trace("delete : {}", id);
        final Token token = get(id);
        tokenRepository.delete(token);
    }
}

