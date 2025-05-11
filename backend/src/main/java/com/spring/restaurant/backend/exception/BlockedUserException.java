package com.spring.restaurant.backend.exception;

import org.springframework.security.core.AuthenticationException;

public class BlockedUserException  extends AuthenticationException {

    public BlockedUserException(String message) {
        super(message);
    }

    public BlockedUserException(String msg, Throwable t) {
        super(msg, t);
    }

}
