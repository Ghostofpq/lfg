package com.gop.lfg.data.models;

import com.gop.lfg.utils.Location;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "profiles")
public abstract class Profile {
    @Id
    private String id;
    private ProfileType profileType;
    private String userId;
    private Float level;
    private Location location;

    public final static String FIELD_LOCATION = "location";
    public final static String FIELD_LEVEL = "level";

    public enum ProfileType {
        MUSICIAN("musician"),
        BOARGAME("boardgame");

        private String textValue;

        ProfileType(String s) {
            textValue = s;
        }

        public String toString() {
            return textValue;
        }
    }

}

