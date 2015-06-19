package com.gop.lfg.utils;

import com.gop.lfg.data.models.Profile;
import lombok.Data;

/**
 * Created by VMPX4526 on 19/06/2015.
 */
@Data
public abstract class ProfileRequest {
    protected Profile.ProfileType type;
    protected Location location;
    protected Float level;
}
