package com.gop.lfg.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.crypto.codec.Base64;

import java.security.SecureRandom;
import java.util.*;

@Data
@Document(collection = "users")
public class User {
    public final static String ROLE_USER = "user";
    public final static String ROLE_ADMIN = "admin";

    public User() {
        tokens = new HashMap<>();
        profiles = new HashSet<>();
        roles = new HashSet<>();
        follows = new HashSet<>();
        followers = new HashSet<>();
        roles.add(ROLE_USER);
    }

    @Id
    private String id;

    @Indexed(unique = true)
    private String login;

    @Indexed(unique = true)
    private String email;

    private String encodedPassword;
    private String salt;

    private long creationTs;
    private long updateTs;

    private Set<String> roles;

    private Map<String, String> tokens;
    private Set<String> profiles;
    private Set<String> follows;
    private Set<String> followers;

    public void setPassword(String password) {
        final Random r = new SecureRandom();
        byte[] salt = new byte[32];
        r.nextBytes(salt);
        setSalt(new String(Base64.encode(salt)));
        setEncodedPassword(new ShaPasswordEncoder(256).encodePassword(password, getSalt()));
    }

    public boolean isPassword(String password) {
        return new ShaPasswordEncoder(256).isPasswordValid(encodedPassword, password, salt);
    }
}
