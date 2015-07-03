package com.gop.lfg.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gop.lfg.data.models.EncodedToken;
import com.gop.lfg.data.models.Token;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.ByteUtil;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.Key;

/**
 * Created by VMPX4526 on 02/07/2015.
 */
@Slf4j
@Component("JwtService")
public class JwtService {

    private final JsonWebEncryption jwe = new JsonWebEncryption();
    private final Key key = new AesKey(ByteUtil.randomBytes(16));
    private final String keyManagementAlgorithm = KeyManagementAlgorithmIdentifiers.A128KW;
    private final String contentEncryptionAlgorithm = ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        log.info("JwtService started !");
        jwe.setAlgorithmHeaderValue(keyManagementAlgorithm);
        jwe.setEncryptionMethodHeaderParameter(contentEncryptionAlgorithm);
        jwe.setKey(key);
    }

    public EncodedToken encode(Token o) throws JsonProcessingException, JoseException {
        jwe.setPayload(mapper.writeValueAsString(o));
        return new EncodedToken(jwe.getCompactSerialization());
    }

    public Token decode(String s) throws IOException, JoseException {
        jwe.setCompactSerialization(s);
        return mapper.readValue(jwe.getPayload(), Token.class);
    }

}
