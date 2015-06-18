package com.gop.lfg.controllers;

import com.gop.lfg.LfgApplication;
import com.gop.lfg.data.models.Token;
import com.gop.lfg.data.models.User;
import com.gop.lfg.data.repositories.TokenRepository;
import com.gop.lfg.data.repositories.UserRepository;
import com.gop.lfg.exceptions.ErrorMessage;
import com.gop.lfg.security.CustomAuthenticationFilter;
import com.gop.lfg.utils.TokenRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LfgApplication.class)
@WebIntegrationTest(randomPort = true)
public class TokenControllerIT {
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8")
    );
    @Value("${local.server.port}")
    private String localPort;
    private String baseURI;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    private RestTemplate template;

    @Data
    public class CustomException extends IOException {
        private Map properties;
    }

    @PostConstruct
    private void init() {
        template = new RestTemplate();
        baseURI = "http://localhost:" + localPort;
        cleanDatabases();
    }

    private static String USER_1_EMAIL = "bob@bob.bob";
    private static String USER_1_PASS = "bobix";
    private static String USER_1_LOGIN = "bob";
    private static String USER_1_TOKEN_NAME = "test";

    private static String EXPIRED_TOKEN_ACCESS_TOKEN = "accessTokenExpiredToken";
    private static String EXPIRED_TOKEN_REFRESH_TOKEN = "refreshTokenExpiredToken";
    private static String EXPIRED_TOKEN_NAME = "bob";


    @Before
    public void setupTestContext() {
        User user1 = new User();
        user1.setLogin(USER_1_LOGIN);
        user1.setEmail(USER_1_EMAIL);
        user1.setPassword(USER_1_PASS);
        user1 = userRepository.save(user1);

        Token expiredToken = new Token();
        expiredToken.setAccessToken(EXPIRED_TOKEN_ACCESS_TOKEN);
        expiredToken.setTokenRefresh(EXPIRED_TOKEN_REFRESH_TOKEN);
        expiredToken.setIssuedAt(DateTime.now().minusDays(1).getMillis());
        expiredToken.setExpiresAt(DateTime.now().minusMillis(1).getMillis());
        expiredToken.setUserId(user1.getId());
        expiredToken = tokenRepository.save(expiredToken);
        user1.getTokens().put(EXPIRED_TOKEN_NAME, expiredToken.getId());
        userRepository.save(user1);
    }

    private void cleanDatabases() {
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    private void printDatabase() {
        log.debug("database content <=========================");
        log.debug("userRepository :");
        for (User u : userRepository.findAll()) {
            log.debug(u.toString());
        }
        log.debug("tokenRepository :");
        for (Token t : tokenRepository.findAll()) {
            log.debug(t.toString());
        }
    }

    @After
    public void cleanTestContext() {
        printDatabase();
        cleanDatabases();
    }

    @Test
    public void createTokenForUser1() throws Exception {
        logTestName("createTokenForUser1");

        final TokenRequest tokenRequest = new TokenRequest(USER_1_LOGIN, USER_1_PASS, USER_1_TOKEN_NAME);

        final ResponseEntity<Token> tokenCreationResponse = template.postForEntity(baseURI + "/api/token/create", tokenRequest, Token.class);
        assertEquals(tokenCreationResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(tokenCreationResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
    }

    @Test
    public void getCreatedTokenForUser1() throws Exception {
        logTestName("getCreatedTokenForUser1");

        final TokenRequest tokenRequest = new TokenRequest(USER_1_LOGIN, USER_1_PASS, USER_1_TOKEN_NAME);

        final ResponseEntity<Token> tokenCreationResponse = template.postForEntity(baseURI + "/api/token/create", tokenRequest, Token.class);
        assertEquals(tokenCreationResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(tokenCreationResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
        final Token token = tokenCreationResponse.getBody();

        final HttpHeaders headers = new HttpHeaders();
        headers.set(CustomAuthenticationFilter.HEADER_ACCESS_TOKEN, token.getAccessToken());
        final HttpEntity entity = new HttpEntity<>(headers);

        final ResponseEntity<Token> getTokenResponse = template.exchange(baseURI + "/api/token/info", HttpMethod.GET, entity, Token.class);
        assertEquals(getTokenResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(getTokenResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
        final Token tokenInfo = getTokenResponse.getBody();
        assertEquals(tokenInfo.getId(), token.getId());
        assertEquals(tokenInfo.getAccessToken(), token.getAccessToken());
        assertEquals(tokenInfo.getTokenRefresh(), token.getTokenRefresh());
        assertEquals(tokenInfo.getIssuedAt(), token.getIssuedAt());
        assertEquals(tokenInfo.getExpiresAt(), token.getExpiresAt());
        assertEquals(tokenInfo.getUserId(), token.getUserId());
    }

    @Test
    public void createdTokenForUser1GrantsAccessToHisData() throws Exception {
        logTestName("createdTokenForUser1GrantsAccessToHisData");

        final TokenRequest tokenRequest = new TokenRequest(USER_1_LOGIN, USER_1_PASS, USER_1_TOKEN_NAME);

        final ResponseEntity<Token> tokenCreationResponse = template.postForEntity(baseURI + "/api/token/create", tokenRequest, Token.class);
        assertEquals(tokenCreationResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(tokenCreationResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
        final Token token = tokenCreationResponse.getBody();

        final HttpHeaders headers = new HttpHeaders();
        headers.set(CustomAuthenticationFilter.HEADER_ACCESS_TOKEN, token.getAccessToken());
        final HttpEntity entity = new HttpEntity<>(headers);

        final ResponseEntity<User> getUserResponse = template.exchange(baseURI + "/api/user/me", HttpMethod.GET, entity, User.class);
        assertEquals(getUserResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(getUserResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
        final User user = getUserResponse.getBody();
        assertEquals(user.getLogin(), USER_1_LOGIN);
        assertEquals(user.getEmail(), USER_1_EMAIL);
        assertTrue(user.getTokens().containsKey(USER_1_TOKEN_NAME));
        assertEquals(user.getTokens().get(USER_1_TOKEN_NAME), token.getId());
    }

    @Test
    public void expiredTokenForUser1ShouldNotGrantsAccessToHisData() throws Exception {
        logTestName("expiredTokenForUser1ShouldNotGrantsAccessToHisData");

        final HttpHeaders headers = new HttpHeaders();
        headers.set(CustomAuthenticationFilter.HEADER_ACCESS_TOKEN, EXPIRED_TOKEN_ACCESS_TOKEN);
        final HttpEntity entity = new HttpEntity<>(headers);
        try {
            template.exchange(baseURI + "/api/user/me", HttpMethod.GET, entity, ErrorMessage.class);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("418"));
        }
    }

    private void logTestName(final String testName) {
        final StringBuilder text = new StringBuilder();
        text.append(System.lineSeparator());
        for (int i = 0; i < testName.length() + 6; i++) {
            text.append("=");
        }
        text.append(System.lineSeparator());
        text.append("|| ");
        text.append(testName);
        text.append(" ||");
        text.append(System.lineSeparator());
        for (int i = 0; i < testName.length() + 6; i++) {
            text.append("=");
        }
        log.debug(text.toString());
    }


}
