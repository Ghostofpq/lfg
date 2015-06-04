package com.gop.lfg.services;

import com.gop.lfg.data.models.User;
import com.gop.lfg.data.repositories.UserRepository;
import com.gop.lfg.exceptions.CustomBadRequestException;
import com.gop.lfg.exceptions.CustomNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component("UserService")
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    private void init() {
        log.info("UserService started !");
    }

    public User add(User user) throws CustomBadRequestException {
        try {
            user.setCreationTs(DateTime.now().getMillis());
            user.setUpdateTs(DateTime.now().getMillis());
            return userRepository.save(user);
        } catch (Exception e) {
            throw new CustomBadRequestException("Could not save new user.");
        }
    }

    public User get(final String id) throws CustomNotFoundException {
        final User user = userRepository.findOne(id);
        if (user != null) {
            return user;
        }
        throw new CustomNotFoundException(id);
    }

    public User getByLogin(final String login) throws CustomNotFoundException {
        final User user = userRepository.findByLogin(login);
        if (user != null) {
            return user;
        }
        throw new CustomNotFoundException(login);
    }

    public User update(User user) throws CustomBadRequestException {
        try {
            user.setUpdateTs(DateTime.now().getMillis());
            return userRepository.save(user);
        } catch (Exception e) {
            throw new CustomBadRequestException("Could not save new user.");
        }
    }

    public void delete(final String id) throws CustomNotFoundException {
        final User user = get(id);
        userRepository.delete(user);
    }

    public Page<User> getAll(final int pageNumber, final int size) {
        return userRepository.findAll(new PageRequest(pageNumber, size));
    }
}

