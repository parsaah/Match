package org.example.match.community.mapper;

import org.example.match.community.dto.CommunityCreateRequest;
import org.example.match.community.dto.CommunityResponse;
import org.example.match.community.dto.CommunityUpdateRequest;
import org.example.match.community.entity.Community;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommunityMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "members", ignore = true)
    Community toEntity(CommunityCreateRequest request);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    CommunityResponse toResponse(Community community);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "members", ignore = true)
    void updateEntity(
            CommunityUpdateRequest request,
            @MappingTarget Community community
    );
}