package com.gop.lfg.services;

import com.gop.lfg.data.models.Profile;
import com.gop.lfg.data.repositories.ProfileRepository;
import com.gop.lfg.exceptions.CustomBadRequestException;
import com.gop.lfg.exceptions.CustomNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component("ProfileService")
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    @PostConstruct
    private void init() {
        log.info("ProfileService started !");
    }

    public Profile add(Profile profile) throws CustomBadRequestException {
        log.trace("add : {}", profile);
        try {
            return profileRepository.save(profile);
        } catch (Exception e) {
            throw new CustomBadRequestException("Could not save new Profile.");
        }
    }

    public Profile get(final String id) throws CustomNotFoundException {
        log.trace("get : {}", id);
        final Profile Profile = profileRepository.findOne(id);
        if (Profile != null) {
            return Profile;
        }
        throw new CustomNotFoundException(id);
    }

    public Profile update(Profile profile) throws CustomBadRequestException {
        log.trace("update : {}", profile);
        try {
            return profileRepository.save(profile);
        } catch (Exception e) {
            throw new CustomBadRequestException("Could not save Profile.");
        }
    }

    public void delete(final String id) throws CustomNotFoundException {
        log.trace("delete : {}", id);
        final Profile profile = get(id);
        profileRepository.delete(profile);
    }
}

