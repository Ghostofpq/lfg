package com.gop.lfg.data.models;


import lombok.Data;

@Data
public class MusicianProfile extends Profile {

    private boolean canTutor;
    private MusicianRole musicianRole;

    public MusicianProfile() {
        setProfileType(ProfileType.MUSICIAN);
    }

    public enum MusicianRole {
        GUITAR("guitar"),
        DRUMS("drums"),
        BASS("bass"),
        KEYBOARD("keyboard"),
        SAX("sax"),
        TRUMPET("trumpet"),

        VOCALS("vocals"),

        WRITER("writer"),
        COMPOSER("composer");

        private String textValue;

        MusicianRole(String s) {
            textValue = s;
        }

        public String toString() {
            return textValue;
        }
    }
}
