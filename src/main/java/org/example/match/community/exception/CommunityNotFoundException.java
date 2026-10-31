package org.example.match.community.exception;

import java.util.UUID;

public class CommunityNotFoundException extends RuntimeException {

    public CommunityNotFoundException(UUID id) {
        super("Community not found with id: " + id);
    }
}