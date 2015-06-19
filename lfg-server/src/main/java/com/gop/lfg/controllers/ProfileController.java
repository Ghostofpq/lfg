package com.gop.lfg.controllers;

import com.gop.lfg.data.models.MusicianProfile;
import com.gop.lfg.data.models.Profile;
import com.gop.lfg.data.models.User;
import com.gop.lfg.exceptions.CustomBadRequestException;
import com.gop.lfg.exceptions.CustomNotFoundException;
import com.gop.lfg.services.ProfileService;
import com.gop.lfg.services.UserService;
import com.gop.lfg.utils.Location;
import com.gop.lfg.utils.MusicianProfileRequest;
import com.gop.lfg.utils.ProfileRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/profile")
@Component("ProfileController")
public class ProfileController {
    @Autowired
    private ProfileService profileService;
    @Autowired
    private UserService userService;

    @PostConstruct
    private void init() {
        log.info("ProfileController started !");
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public Profile create(@RequestBody final ProfileRequest profileRequest) throws Exception {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getByLogin((String) authentication.getPrincipal());

        Profile profile = null;
        switch (profileRequest.getType()) {
            case BOARGAME:
                break;
            case MUSICIAN:
                MusicianProfileRequest request = (MusicianProfileRequest) profileRequest;
                profile = new MusicianProfile();
                ((MusicianProfile) profile).setCanTutor(request.isCanTutor());
                ((MusicianProfile) profile).setMusicianRole(request.getRole());
                break;
        }
        if (profile == null) {
            throw new Exception("coin");
        }
        profile.setLocation(profileRequest.getLocation());
        profile.setLevel(profileRequest.getLevel());
        profile.setUserId(user.getId());
        //TODO check unicity of roles per user
        profile = profileService.add(profile);
        user.getProfiles().add(profile.getId());
        userService.update(user);
        return profile;
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Profile get(
            @PathVariable("id") final String id)
            throws CustomNotFoundException {
        log.debug("get({})", id);
        return profileService.get(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Profile update(
            @PathVariable("id") final String id,
            @RequestBody final Map<String, Object> fields)
            throws CustomNotFoundException,
            CustomBadRequestException {
        final Profile profile = profileService.get(id);
        for (final String key : fields.keySet()) {
            updateFieldForProfile(profile, key, fields.get(key));
        }
        return profileService.update(profile);
    }

    private void updateFieldForProfile(final Profile profile, final String field, final Object value) {
        switch (field) {
            case Profile.FIELD_LEVEL:
                profile.setLevel((Float) value);
                break;
            case Profile.FIELD_LOCATION:
                profile.setLocation((Location) value);
                break;
            case MusicianProfile.FIELD_CAN_TUTOR:
                ((MusicianProfile) profile).setCanTutor((boolean) value);
                break;
            default:
                log.error("Unknown field {}", field);
                break;
        }
    }
}
