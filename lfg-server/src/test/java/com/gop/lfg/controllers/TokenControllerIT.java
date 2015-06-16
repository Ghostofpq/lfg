package com.gop.lfg.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gop.lfg.LfgApplication;
import com.gop.lfg.data.models.Token;
import com.gop.lfg.data.models.User;
import com.gop.lfg.data.repositories.TokenRepository;
import com.gop.lfg.data.repositories.UserRepository;
import com.gop.lfg.security.CustomAuthenticationFilter;
import com.gop.lfg.utils.TokenRequest;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

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

    @PostConstruct
    private void init() {
        template = new RestTemplate();
        baseURI = "http://localhost:" + localPort;
    }

    private static String USER_1_EMAIL = "bob@bob.bob";
    private static String USER_1_PASS = "bobix";
    private static String USER_1_LOGIN = "bob";


    @Before
    public void setupTestContext() {
        User user1 = new User();
        user1.setLogin(USER_1_LOGIN);
        user1.setEmail(USER_1_EMAIL);
        user1.setPassword(USER_1_PASS);
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
        log.debug("=================================================");
        log.debug("===============createTokenForUser1===============");
        log.debug("=================================================");

        final TokenRequest tokenRequest = new TokenRequest(USER_1_LOGIN, USER_1_PASS, "test");

        final ResponseEntity<Token> tokenCreationResponse = template.postForEntity(baseURI + "/api/token/create", tokenRequest, Token.class);
        assertEquals(tokenCreationResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(tokenCreationResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
    }

    @Test
    public void createdTokenForUser1GrantsAccessToHisData() throws Exception {
        log.debug("=================================================");
        log.debug("====createdTokenForUser1GrantsAccessToHisData====");
        log.debug("=================================================");

        final TokenRequest tokenRequest = new TokenRequest(USER_1_LOGIN, USER_1_PASS, "test");

        final ResponseEntity<Token> tokenCreationResponse = template.postForEntity(baseURI + "/api/token/create", tokenRequest, Token.class);
        assertEquals(tokenCreationResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(tokenCreationResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
        final Token token = tokenCreationResponse.getBody();

        final HttpHeaders headers = new HttpHeaders();
        headers.set(CustomAuthenticationFilter.HEADER_ACCESS_TOKEN, token.getAccessToken());
        final   HttpEntity entity = new HttpEntity<>(headers);

        final ResponseEntity<User> getUserResponse = template.exchange(baseURI + "/api/user/me", HttpMethod.GET, entity, User.class);
        assertEquals(getUserResponse.getStatusCode(), HttpStatus.OK);
        assertEquals(getUserResponse.getHeaders().getContentType(), APPLICATION_JSON_UTF8);
        final User user = getUserResponse.getBody();
        assertEquals(user.getLogin(), USER_1_LOGIN);
        assertEquals(user.getEmail(), USER_1_EMAIL);
    }
}
