package org.example.match.user.mapper;

import org.example.match.user.dto.AdminUserUpdateRequest;
import org.example.match.user.dto.UserCreateRequest;
import org.example.match.user.dto.UserResponse;
import org.example.match.user.dto.UserUpdateRequest;
import org.example.match.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserCreateRequest request);

    UserResponse toResponse(User user);

    void updateEntity(
            UserUpdateRequest request,
            @MappingTarget User user
    );

    void updateEntityByAdmin(
            AdminUserUpdateRequest request,
            @MappingTarget User user
    );
}