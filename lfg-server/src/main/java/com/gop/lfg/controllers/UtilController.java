package com.gop.lfg.controllers;

import com.gop.lfg.exceptions.CustomNotFoundException;
import com.gop.lfg.exceptions.ErrorMessage;
import com.gop.lfg.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

/**
 * Created by GhostOfPQ on 20/07/2015.
 */
@Slf4j
@Controller
@RequestMapping("/api/utils")
@Component("UtilController")
public class UtilController {
    @Autowired
    private UserService userService;

    @PostConstruct
    private void init() {
        log.info("UtilController started !");
    }

    @RequestMapping(value = "/isLoginFree/{login}", method = RequestMethod.GET)
    @ResponseBody
    public void isLoginFree(@PathVariable("login") final String login) throws CustomNotFoundException {
        userService.getByLogin(login);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseBody
    private ErrorMessage handleNotFoundException(CustomNotFoundException e) {
        log.error(HttpStatus.NOT_FOUND + ":" + e.getMessage());
        return new ErrorMessage(e.getMessage());
    }
}
