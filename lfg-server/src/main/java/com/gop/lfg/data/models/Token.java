package com.gop.lfg.data.models;

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

@Data
@Document(collection = "tokens")
public class Token {
    @Id
    private String id;
    @Indexed
    private String accessToken;
    @Indexed
    private String tokenRefresh;
    @Indexed
    private String userId;
    private long issuedAt;
    private long expiresAt;

    public Token(String userId) {
        final Random randomSeed = new SecureRandom();
        this.accessToken = new BigInteger(130, randomSeed).toString(32);
        this.tokenRefresh = new BigInteger(130, randomSeed).toString(32);

        this.userId = userId;

        this.issuedAt = DateTime.now().getMillis();
        this.expiresAt = issuedAt + 1000 * 60 * 60; //1h
    }
}
