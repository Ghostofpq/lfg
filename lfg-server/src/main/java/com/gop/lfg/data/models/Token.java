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
    @Indexed(unique = true)
    private String accessToken;
    @Indexed(unique = true)
    private String refreshToken;
    @Indexed
    private String userId;
    private long issuedAt;
    private long expiresAt;
    private Set<String> roles;

    public Token(final User user) {
        final Random randomSeed = new SecureRandom();

        this.userId = user.getId();
        this.roles = user.getRoles();

        this.accessToken = new BigInteger(130, randomSeed).toString(64);
        this.refreshToken = new BigInteger(130, randomSeed).toString(32);

        this.issuedAt = DateTime.now().getMillis();
        this.expiresAt = issuedAt + 1000 * 60 * 60; //1h
    }

    public void refresh() {
        final Random randomSeed = new SecureRandom();

        this.accessToken = new BigInteger(130, randomSeed).toString(64);
        this.refreshToken = new BigInteger(130, randomSeed).toString(32);

        this.issuedAt = DateTime.now().getMillis();
        this.expiresAt = issuedAt + 1000 * 60 * 60; //1h
    }

    public boolean isValid() {
        return DateTime.now().getMillis() < expiresAt;
    }
}
