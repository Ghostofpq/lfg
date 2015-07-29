package com.gop.lfg.data.models;

import com.gop.lfg.utils.Location;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "profiles")
public abstract class Profile {
    @Id
    protected String id;
    protected ProfileType profileType;
    protected String userId;
    protected Float level;
    protected Location location;

    public enum ProfileType {
        MUSICIAN("musician"),
        BOARGAME("boardgame"),
        SPORTS("sports");

        private String textValue;

        ProfileType(String s) {
            textValue = s;
        }

        public String toString() {
            return textValue;
        }
    }

}

