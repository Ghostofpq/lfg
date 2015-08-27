package com.gop.lfg.controllers;

import com.gop.lfg.LfgApplication;
import com.gop.lfg.data.models.EncodedToken;
import com.gop.lfg.data.models.Token;
import com.gop.lfg.data.models.User;
import com.gop.lfg.data.repositories.TokenRepository;
import com.gop.lfg.data.repositories.UserRepository;
import com.gop.lfg.security.CustomAuthenticationFilter;
import com.gop.lfg.services.JwtService;
import com.gop.lfg.utils.TokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

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
    @Autowired
    private JwtService jwtService;

    private RestTemplate template;

    @PostConstruct
    private void init() {
        template = new TestRestTemplate();
        template.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return !clientHttpResponse.getStatusCode().is2xxSuccessful();
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                // Do nothing
            }
        });
        baseURI = "http://localhost:" + localPort;
        cleanDatabases();
    }

    private static String USER_1_EMAIL = "bob@bob.bob";
    private static String USER_1_PASS = "bobix";
    private static String USER_1_LOGIN = "bob";
    private static String USER_1_TOKEN_NAME = "test";

    @Before
    public void setupTestContext() {
        User user1 = new User();
        user1.setLogin(USER_1_LOGIN);
        user1.setEmail(USER_1_EMAIL);
        user1.setPassword(USER_1_PASS);
        user1 = userRepository.save(user1);
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

        final ResponseEntity<String> tokenCreationResponse = template
                .postForEntity(baseURI + "/api/token/create", tokenRequest, String.class);
        assertEquals(tokenCreationResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(tokenCreationResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
    }

    @Test
    public void getCreatedTokenForUser1() throws Exception {
        logTestName("getCreatedTokenForUser1");

        final TokenRequest tokenRequest = new TokenRequest(USER_1_LOGIN, USER_1_PASS, USER_1_TOKEN_NAME);

        final ResponseEntity<EncodedToken> tokenCreationResponse = template
                .postForEntity(baseURI + "/api/token/create", tokenRequest, EncodedToken.class);
        assertEquals(tokenCreationResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(tokenCreationResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
        final Token token = jwtService.decode(tokenCreationResponse.getBody().getValue());

        final HttpHeaders headers = new HttpHeaders();
        headers.set(CustomAuthenticationFilter.HEADER_TOKEN, jwtService.encode(token).getValue());
        final HttpEntity entity = new HttpEntity<>(headers);

        final ResponseEntity<Token> getTokenResponse = template
                .exchange(baseURI + "/api/token/info", HttpMethod.GET, entity, Token.class);
        assertEquals(getTokenResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(getTokenResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
        final Token tokenInfo = getTokenResponse.getBody();
        assertEquals(tokenInfo.getId(), token.getId());
        assertEquals(tokenInfo.getAccessToken(), token.getAccessToken());
        assertEquals(tokenInfo.getRefreshToken(), token.getRefreshToken());
        assertEquals(tokenInfo.getIssuedAt(), token.getIssuedAt());
        assertEquals(tokenInfo.getExpiresAt(), token.getExpiresAt());
        assertEquals(tokenInfo.getUserId(), token.getUserId());
    }

    @Test
    public void createdTokenForUser1GrantsAccessToHisData() throws Exception {
        logTestName("createdTokenForUser1GrantsAccessToHisData");

        final TokenRequest tokenRequest = new TokenRequest(USER_1_LOGIN, USER_1_PASS, USER_1_TOKEN_NAME);

        final ResponseEntity<EncodedToken> tokenCreationResponse = template
                .postForEntity(baseURI + "/api/token/create", tokenRequest, EncodedToken.class);
        assertEquals(tokenCreationResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(tokenCreationResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
        final Token token = jwtService.decode(tokenCreationResponse.getBody().getValue());

        final HttpHeaders headers = new HttpHeaders();
        headers.set(CustomAuthenticationFilter.HEADER_TOKEN, jwtService.encode(token).getValue());
        final HttpEntity entity = new HttpEntity<>(headers);

        final ResponseEntity<User> getUserResponse = template
                .exchange(baseURI + "/api/user/me", HttpMethod.GET, entity, User.class);
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

        final TokenRequest tokenRequest = new TokenRequest(USER_1_LOGIN, USER_1_PASS, USER_1_TOKEN_NAME);

        final ResponseEntity<EncodedToken> tokenCreationResponse = template
                .postForEntity(baseURI + "/api/token/create", tokenRequest, EncodedToken.class);
        assertEquals(tokenCreationResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(tokenCreationResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
        final Token token = jwtService.decode(tokenCreationResponse.getBody().getValue());
        token.setExpiresAt(DateTime.now().minusMinutes(1).getMillis());

        final HttpHeaders headers = new HttpHeaders();
        headers.set(CustomAuthenticationFilter.HEADER_TOKEN, jwtService.encode(token).getValue());
        final HttpEntity entity = new HttpEntity<>(headers);
        final ResponseEntity<User> getUserResponse = template
                .exchange(baseURI + "/api/user/me", HttpMethod.GET, entity, User.class);
        assertEquals(getUserResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(getUserResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
        final Token refreshedToken = jwtService
                .decode(getUserResponse.getHeaders().getFirst(CustomAuthenticationFilter.HEADER_TOKEN));
        assertEquals(refreshedToken.getId(), token.getId());
        assertEquals(refreshedToken.getUserId(), token.getUserId());
        assertEquals(refreshedToken.getRoles(), token.getRoles());
        assertNotEquals(refreshedToken.getExpiresAt(), token.getExpiresAt());
        assertNotEquals(refreshedToken.getAccessToken(), token.getAccessToken());
        assertNotEquals(refreshedToken.getRefreshToken(), token.getRefreshToken());
    }

    @Test
    public void expiredTokenForUser1WithInvalidRefreshNotGrantsAccessToHisData() throws Exception {
        logTestName("expiredTokenForUser1ShouldNotGrantsAccessToHisData");

        final TokenRequest tokenRequest = new TokenRequest(USER_1_LOGIN, USER_1_PASS, USER_1_TOKEN_NAME);

        final ResponseEntity<EncodedToken> tokenCreationResponse = template
                .postForEntity(baseURI + "/api/token/create", tokenRequest, EncodedToken.class);
        assertEquals(tokenCreationResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(tokenCreationResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
        final Token token = jwtService.decode(tokenCreationResponse.getBody().getValue());
        token.setExpiresAt(DateTime.now().minusMinutes(1).getMillis());
        token.setRefreshToken("invalid");
        final HttpHeaders headers = new HttpHeaders();
        headers.set(CustomAuthenticationFilter.HEADER_TOKEN, jwtService.encode(token).getValue());
        final HttpEntity entity = new HttpEntity<>(headers);
        final ResponseEntity<User> getUserResponse = template
                .exchange(baseURI + "/api/user/me", HttpMethod.GET, entity, User.class);
        assertEquals(HttpStatus.FORBIDDEN, getUserResponse.getStatusCode());
        assertEquals(getUserResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
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
