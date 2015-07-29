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
        GUITAR("Guitar"),
        ELECTRIC_GUITAR("Electric guitar"),
        DRUMS("Drums"),
        BASS("Bass"),
        KEYBOARD("Keyboard"),
        SAX("Sax"),
        TRUMPET("Trumpet"),

        VOCALS("Vocals"),

        WRITER("Writer"),
        COMPOSER("Composer"),
        MANAGER("Manager");

        private String textValue;

        MusicianRole(String s) {
            textValue = s;
        }

        public String toString() {
            return textValue;
        }
    }
}
