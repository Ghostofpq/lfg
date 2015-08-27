package com.gop.lfg.exceptions;

import java.io.IOException;

public class CustomNotAuthorizedException extends IOException {
    public CustomNotAuthorizedException() {
        super("You don't have the rights to access this page");
    }
}
