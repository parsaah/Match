package org.example.match.community.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.match.community.enums.CommunityType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CommunityResponse {

    private UUID id;

    private String name;

    private String description;

    private UUID categoryId;

    private String categoryName;

    private CommunityType type;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}