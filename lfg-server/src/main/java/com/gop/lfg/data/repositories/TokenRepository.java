package com.gop.lfg.data.repositories;

import com.gop.lfg.data.models.Token;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("TokenRepository")
public interface TokenRepository extends PagingAndSortingRepository<Token, String> {
    Token findByAccessToken(final String accessToken);

    Token findByRefreshToken(final String refreshToken);

    List<Token> findByUserId(final String userId);
}
