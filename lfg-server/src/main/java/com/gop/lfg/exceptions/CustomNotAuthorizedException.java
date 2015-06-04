package com.gop.lfg.exceptions;

/**
 * @author GhostOfPQ
 */
public class CustomNotAuthorizedException extends Exception {
    public CustomNotAuthorizedException() {
        super("You don't have the rights to access this page");
    }
}
