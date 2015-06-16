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
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.nio.charset.Charset;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LfgApplication.class)
@WebAppConfiguration
public class TokenControllerIT {
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8")
    );

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private Filter springSecurityFilterChain;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    private void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();

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
        final TokenRequest tokenRequest = new TokenRequest(USER_1_LOGIN, USER_1_PASS, "test");

        mockMvc.perform(post("/api/token/create").content(mapper.writeValueAsString(tokenRequest)).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8));
    }

    @Test
    public void createdTokenForUser1GrantsAccessToHisData() throws Exception {
        final TokenRequest tokenRequest = new TokenRequest(USER_1_LOGIN, USER_1_PASS, "test");

        final MvcResult res = mockMvc.perform(post("/api/token/create").content(mapper.writeValueAsString(tokenRequest)).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andReturn();
        final Token token = mapper.readValue(res.getResponse().getContentAsString(), Token.class);

        mockMvc.perform(get("/api/user/me").contentType(APPLICATION_JSON_UTF8).header(CustomAuthenticationFilter.HEADER_ACCESS_TOKEN, token.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$." + User.FIELD_LOGIN, is(USER_1_LOGIN)))
                .andExpect(jsonPath("$." + User.FIELD_EMAIL, is(USER_1_EMAIL)));
    }
}
