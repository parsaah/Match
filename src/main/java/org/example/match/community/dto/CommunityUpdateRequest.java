package org.example.match.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.match.community.enums.CommunityType;

import java.util.UUID;

@Getter
@Setter
public class CommunityUpdateRequest {

    @NotBlank(message = "Community name is required")
    @Size(max = 255, message = "Community name must not exceed 255 characters")
    private String name;

    @Size(max = 2000, message = "Community description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Category is required")
    private UUID categoryId;

    @NotNull(message = "Community type is required")
    private CommunityType type;
}