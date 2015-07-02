package com.gop.lfg.security;

import com.gop.lfg.data.models.Token;
import com.gop.lfg.data.models.User;
import com.gop.lfg.exceptions.CustomExpiredTokenExceptionException;
import com.gop.lfg.exceptions.CustomNotFoundException;
import com.gop.lfg.services.TokenService;
import com.gop.lfg.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


@Slf4j
@Deprecated
public class CustomAuthenticationManager implements AuthenticationProvider {

    private TokenService tokenService;
    private UserService userService;

    public CustomAuthenticationManager(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accessToken = (String) authentication.getDetails();
        if (accessToken == null) {
            log.warn("No Token");
        } else {
            try {
                log.trace("accessToken :" + accessToken);
                final Token token = tokenService.getByAccessToken(accessToken);
                log.trace("token : " + token.toString());
                if (token.getExpiresAt() > DateTime.now().getMillis()) {
                    final User user = userService.get(token.getUserId());
                    log.trace("user : " + user.toString());
                    String name = "";
                    for (String key : user.getTokens().keySet()) {
                        if (user.getTokens().get(key).equals(token.getId())) {
                            name = key;
                            break;
                        }
                    }
                 //   ((CustomAuthentication) authentication).completeAuth(user.getId(), name, user.getRoles());
                    log.trace("authentication : " + ((CustomAuthentication) authentication).toString());
                } else {
                    throw new CustomExpiredTokenExceptionException("Expired Token");
                }
            } catch (CustomNotFoundException e) {
                throw new CustomAuthenticationException("Auth failed", e.getCause());
            }
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(CustomAuthentication.class);
    }
}
