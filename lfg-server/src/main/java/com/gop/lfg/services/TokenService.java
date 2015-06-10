package com.gop.lfg.services;

import com.gop.lfg.data.models.Token;
import com.gop.lfg.data.models.User;
import com.gop.lfg.data.repositories.TokenRepository;
import com.gop.lfg.data.repositories.UserRepository;
import com.gop.lfg.exceptions.CustomBadRequestException;
import com.gop.lfg.exceptions.CustomNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component("TokenService")
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    @PostConstruct
    private void init() {
        log.info("TokenService started !");
    }

    public Token add(Token token) throws CustomBadRequestException {
        try {
            return tokenRepository.save(token);
        } catch (Exception e) {
            throw new CustomBadRequestException("Could not save new token.");
        }
    }

    public Token get(final String id) throws CustomNotFoundException {
        final Token token = tokenRepository.findOne(id);
        if (token != null) {
            return token;
        }
        throw new CustomNotFoundException(id);
    }

    public Token update(Token token) throws CustomBadRequestException {
        try {
            return tokenRepository.save(token);
        } catch (Exception e) {
            throw new CustomBadRequestException("Could not save token.");
        }
    }

    public void delete(final String id) throws CustomNotFoundException {
        final Token token = get(id);
        tokenRepository.delete(token);
    }
}

