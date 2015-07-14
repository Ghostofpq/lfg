package com.gop.lfg.controllers;

import com.gop.lfg.data.models.User;
import com.gop.lfg.exceptions.CustomBadRequestException;
import com.gop.lfg.exceptions.CustomNotFoundException;
import com.gop.lfg.exceptions.ErrorMessage;
import com.gop.lfg.services.TokenService;
import com.gop.lfg.services.UserService;
import com.gop.lfg.utils.UserCreationRequest;
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
    public User create(@RequestBody final UserCreationRequest userToCreate) throws CustomBadRequestException {
        log.debug("In create with {}", userToCreate.toString());
        final User user = new User();
        // Set Login
        user.setLogin(userToCreate.getLogin());
        // Set Email
        user.setEmail(userToCreate.getEmail());
        // Set Password
        user.setPassword(userToCreate.getPassword());
        // Save
        return userService.add(user);
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
            @RequestBody final Map<String, Object> fields)
            throws CustomNotFoundException,
            CustomBadRequestException {
        final User user = userService.get(id);
        for (final String key : fields.keySet()) {
            updateFieldForUser(user, key, fields.get(key));
        }
        return userService.update(user);
    }

    @RequestMapping(value = "/{id}/{field}", method = RequestMethod.PUT)
    @ResponseBody
    public User updateField(
            @PathVariable("id") final String id,
            @PathVariable("field") final String field,
            @RequestBody final Object value)
            throws CustomNotFoundException,
            CustomBadRequestException {
        final User user = userService.get(id);
        updateFieldForUser(user, field, value);
        return userService.update(user);
    }

    private void updateFieldForUser(final User user, final String field, final Object value) {
        switch (field) {
            case User.FIELD_LOGIN:
                user.setLogin((String) value);
                break;
            case User.FIELD_PASSWORD:
                user.setPassword((String) value);
                break;
            case User.FIELD_EMAIL:
                user.setEmail((String) value);
                break;
            default:
                log.error("Unknown field {}", field);
                break;
        }
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
