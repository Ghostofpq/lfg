package com.gop.lfg.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Set;

@Data
@ToString
@Document(collection = "tokens")
@NoArgsConstructor
@AllArgsConstructor
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
    private Set<String> roles;

    public Token(User user) {
        final Random randomSeed = new SecureRandom();
        this.accessToken = new BigInteger(130, randomSeed).toString(64);
        this.tokenRefresh = new BigInteger(130, randomSeed).toString(32);

        this.userId = user.getId();
        this.roles = user.getRoles();

        this.issuedAt = DateTime.now().getMillis();
        this.expiresAt = issuedAt + 1000 * 60 * 60; //1h
    }
}
