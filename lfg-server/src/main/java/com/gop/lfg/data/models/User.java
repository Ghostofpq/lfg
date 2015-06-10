package com.gop.lfg.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import java.security.SecureRandom;
import java.util.*;

/**
 * @author GhostOfPQ
 */
@Data
@Document(collection = "users")
public class User {
    public final static String FIELD_LOGIN = "login";
    public final static String FIELD_PASSWORD = "password";
    public final static String FIELD_EMAIL = "email";

    public User() {
        tokens = new HashMap<>();
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

    private Map<String,String> tokens;

    public void setPassword(String password) {
        final Random r = new SecureRandom();
        byte[] salt = new byte[32];
        r.nextBytes(salt);
        setSalt(new String(salt));
        setEncodedPassword(new ShaPasswordEncoder(256).encodePassword(password, getSalt()));
    }
}
