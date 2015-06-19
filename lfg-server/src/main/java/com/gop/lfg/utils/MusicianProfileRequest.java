package com.gop.lfg.utils;

import com.gop.lfg.data.models.MusicianProfile;
import com.gop.lfg.data.models.Profile;
import lombok.Data;

/**
 * Created by VMPX4526 on 19/06/2015.
 */
@Data
public class MusicianProfileRequest extends ProfileRequest {
    private boolean canTutor;
    private MusicianProfile.MusicianRole role;

    public MusicianProfileRequest(Location location, Float level, boolean canTutor, MusicianProfile.MusicianRole role) {
        this.type = Profile.ProfileType.MUSICIAN;
        this.location = location;
        this.level = level;
        this.canTutor = canTutor;
        this.role = role;
    }
}
