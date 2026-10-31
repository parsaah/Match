package org.example.match.user.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super("User not found with this option: " + message);
    }

    public UserNotFoundException(UUID id) {
        super("User not found with id: " + id);
    }
}
