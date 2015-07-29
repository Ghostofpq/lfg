package com.gop.lfg.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gop.lfg.LfgApplication;
import com.gop.lfg.data.models.EncodedToken;
import com.gop.lfg.data.models.Profile;
import com.gop.lfg.data.models.Token;
import com.gop.lfg.data.models.User;
import com.gop.lfg.data.repositories.ProfileRepository;
import com.gop.lfg.data.repositories.TokenRepository;
import com.gop.lfg.data.repositories.UserRepository;
import com.gop.lfg.exceptions.ErrorMessage;
import com.gop.lfg.security.CustomAuthenticationFilter;
import com.gop.lfg.services.JwtService;
import com.gop.lfg.utils.TokenRequest;
import com.gop.lfg.utils.UserCreationRequest;
import lombok.extern.slf4j.Slf4j;
import org.h2.command.ddl.CreateUser;
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
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LfgApplication.class)
@WebIntegrationTest(randomPort = true)
public class UserControllerIT {
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
    private ProfileRepository profileRepository;
    @Autowired
    private JwtService jwtService;

    private TestRestTemplate template;

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

    private static String USER_2_EMAIL = "aze@aze.aze";
    private static String USER_2_PASS = "aze";
    private static String USER_2_LOGIN = "aze";
    private static String USER_2_TOKEN_NAME = "test";

    @Before
    public void setupTestContext() {
        User user1 = new User();
        user1.setLogin(USER_1_LOGIN);
        user1.setEmail(USER_1_EMAIL);
        user1.setPassword(USER_1_PASS);
        userRepository.save(user1);

        User user2 = new User();
        user2.setLogin(USER_2_LOGIN);
        user2.setEmail(USER_2_EMAIL);
        user2.setPassword(USER_2_PASS);
        userRepository.save(user2);
    }

    private void cleanDatabases() {
        userRepository.deleteAll();
        tokenRepository.deleteAll();
        profileRepository.deleteAll();
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
        log.debug("profileRepository :");
        for (Profile p : profileRepository.findAll()) {
            log.debug(p.toString());
        }
    }

    @After
    public void cleanTestContext() {
        printDatabase();
        cleanDatabases();
    }

    @Test
    public void createUser() throws Exception {
        logTestName("createUser");
        final String user3Email = "user3email";
        final String user3Login = "user3Login";
        final String user3Password = "user3Password";
        final String emptyString ="";

        final ResponseEntity<User> userCreationRequestNoLogin = template
                .postForEntity(baseURI + "/api/user", new UserCreationRequest(null, null, null), User.class);
        assertEquals(userCreationRequestNoLogin.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(userCreationRequestNoLogin.getHeaders().getContentType(), APPLICATION_JSON_UTF8);

        final ResponseEntity<User> userCreationRequestEmptyLogin = template
                .postForEntity(baseURI + "/api/user", new UserCreationRequest(emptyString, null, null), User.class);
        assertEquals(userCreationRequestEmptyLogin.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(userCreationRequestEmptyLogin.getHeaders().getContentType(), APPLICATION_JSON_UTF8);

        final ResponseEntity<User> userCreationRequestNoEmail = template
                .postForEntity(baseURI + "/api/user", new UserCreationRequest(user3Login, null, null), User.class);
        assertEquals(userCreationRequestNoEmail.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(userCreationRequestNoEmail.getHeaders().getContentType(), APPLICATION_JSON_UTF8);

        final ResponseEntity<User> userCreationRequestEmptyEmail = template
                .postForEntity(baseURI + "/api/user", new UserCreationRequest(user3Login, emptyString, null), User.class);
        assertEquals(userCreationRequestEmptyEmail.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(userCreationRequestEmptyEmail.getHeaders().getContentType(), APPLICATION_JSON_UTF8);

        final ResponseEntity<User> userCreationRequestNoPassword = template
                .postForEntity(baseURI + "/api/user", new UserCreationRequest(user3Login, user3Email, null), User.class);
        assertEquals(userCreationRequestNoPassword.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(userCreationRequestNoPassword.getHeaders().getContentType(), APPLICATION_JSON_UTF8);

        final ResponseEntity<User> userCreationRequestEmptyPassword = template
                .postForEntity(baseURI + "/api/user", new UserCreationRequest(user3Login, user3Email, emptyString), User.class);
        assertEquals(userCreationRequestEmptyPassword.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(userCreationRequestEmptyPassword.getHeaders().getContentType(), APPLICATION_JSON_UTF8);

        final ResponseEntity<User> userCreationRequest = template
                .postForEntity(baseURI + "/api/user", new UserCreationRequest(user3Login, user3Email, user3Password), User.class);
        assertEquals(HttpStatus.OK, userCreationRequest.getStatusCode());
        assertEquals(APPLICATION_JSON_UTF8, userCreationRequestEmptyPassword.getHeaders().getContentType());
        assertEquals(user3Login, userCreationRequest.getBody().getLogin());
        assertEquals(user3Email, userCreationRequest.getBody().getEmail());
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
