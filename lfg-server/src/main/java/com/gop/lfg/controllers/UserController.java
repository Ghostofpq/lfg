package com.gop.lfg.controllers;

import com.google.common.base.Strings;
import com.gop.lfg.data.models.User;
import com.gop.lfg.exceptions.CustomBadRequestException;
import com.gop.lfg.exceptions.CustomNotFoundException;
import com.gop.lfg.exceptions.ErrorMessage;
import com.gop.lfg.services.TokenService;
import com.gop.lfg.services.UserService;
import com.gop.lfg.utils.UserCreationRequest;
import com.gop.lfg.utils.UserUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/user")
@Component("UserController")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;

    @PostConstruct
    private void init() {
        log.info("UserController started !");
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public User create(@RequestBody final UserCreationRequest userCreationRequest) throws CustomBadRequestException {
        log.debug("In create with {}", userCreationRequest.toString());
        final User user = new User();
        // LOGIN
        if (!Strings.isNullOrEmpty(userCreationRequest.getLogin())) {
            if (userService.loginIsFreeToUse(userCreationRequest.getLogin())) {
                user.setLogin(userCreationRequest.getLogin());
            } else {
                throw new CustomBadRequestException("Login is already used");
            }
        } else {
            throw new CustomBadRequestException("User creation request need a login");
        }
        // EMAIL
        if (!Strings.isNullOrEmpty(userCreationRequest.getEmail())) {
            if (userService.emailIsFreeToUse(userCreationRequest.getEmail())) {
                user.setEmail(userCreationRequest.getEmail());
            } else {
                throw new CustomBadRequestException("Email is already used");
            }
        } else {
            throw new CustomBadRequestException("User creation request need an email");
        }
        // PASSWORD
        if (!Strings.isNullOrEmpty(userCreationRequest.getPassword())) {
            user.setPassword(userCreationRequest.getPassword());
        } else {
            throw new CustomBadRequestException("User creation request need a password");
        }
        return userService.add(user);
    }

    @RequestMapping(value = "/follow/{id}", method = RequestMethod.GET)
    @ResponseBody
    public User follow(@PathVariable("id") final String otherId) throws CustomNotFoundException, CustomBadRequestException {
        final User user = getSelf();
        final User other = get(otherId);
        user.getFollows().add(otherId);
        other.getFollowers().add(user.getId());
        userService.update(other);
        return userService.update(user);
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    @ResponseBody
    public User getSelf() throws CustomNotFoundException {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.get((String) authentication.getPrincipal());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public User get(
            @PathVariable("id") final String id)
            throws CustomNotFoundException {
        log.debug("get({})", id);
        return userService.get(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public User update(
            @PathVariable("id") final String id,
            @RequestBody final UserUpdateRequest userUpdateRequest)
            throws CustomNotFoundException,
            CustomBadRequestException {
        final User user = userService.get(id);
        if (!Strings.isNullOrEmpty(userUpdateRequest.getLogin())) {
            if (userService.loginIsFreeToUse(userUpdateRequest.getLogin())) {
                user.setLogin(userUpdateRequest.getLogin());
            } else {
                throw new CustomBadRequestException("Login is already used");
            }
        }
        if (!Strings.isNullOrEmpty(userUpdateRequest.getEmail())) {
            if (userService.emailIsFreeToUse(userUpdateRequest.getEmail())) {
                user.setEmail(userUpdateRequest.getEmail());
            } else {
                throw new CustomBadRequestException("Email is already used");
            }
        }
        if (!Strings.isNullOrEmpty(userUpdateRequest.getPassword())) {
            user.setPassword(userUpdateRequest.getPassword());
        }
        return userService.update(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(
            @PathVariable("id") final String id)
            throws CustomNotFoundException {
        final User user = userService.get(id);
        for (String tokenId : user.getTokens().values()) {
            try {
                tokenService.delete(tokenId);
            } catch (Exception e) {
                //nevermind
            }
        }
        userService.delete(id);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public Page<User> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") final Integer pageNumber,
            @RequestParam(value = "size", required = false, defaultValue = "10") final Integer size) {
        return userService.getAll(pageNumber, size);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseBody
    private ErrorMessage handleNotFoundException(CustomNotFoundException e) {
        log.error(HttpStatus.NOT_FOUND + ":" + e.getMessage());
        return new ErrorMessage(e.getMessage());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomBadRequestException.class)
    @ResponseBody
    private ErrorMessage handleBadRequestException(CustomBadRequestException e) {
        log.error(HttpStatus.BAD_REQUEST + ":" + e.getMessage());
        return new ErrorMessage(e.getMessage());
    }
}
