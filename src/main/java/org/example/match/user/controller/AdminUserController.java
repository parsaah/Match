package org.example.match.user.controller;

import org.example.match.user.dto.AdminUserUpdateRequest;
import org.example.match.user.dto.UserResponse;
import org.example.match.user.entity.User;
import org.example.match.user.mapper.UserMapper;
import org.example.match.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public AdminUserController(
            UserService userService,
            UserMapper userMapper) {

        this.userService = userService;
        this.userMapper = userMapper;
    }

    // Update User By Admin
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUserByAdmin(
            @PathVariable UUID id,
            @RequestBody AdminUserUpdateRequest request) {

        User user = userService.updateUserByAdmin(id, request);

        return ResponseEntity.ok(
                userMapper.toResponse(user)
        );
    }
}