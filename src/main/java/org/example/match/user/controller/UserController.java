package org.example.match.user.controller;

import org.example.match.user.dto.UserCreateRequest;
import org.example.match.user.dto.UserResponse;
import org.example.match.user.dto.UserUpdateRequest;
import org.example.match.user.entity.User;
import org.example.match.user.enums.UserRole;
import org.example.match.user.mapper.UserMapper;
import org.example.match.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(
            UserService userService,
            UserMapper userMapper) {

        this.userService = userService;
        this.userMapper = userMapper;
    }

    // Create User
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody UserCreateRequest request) {

        User user = userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.toResponse(user));
    }

    // Read All Users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(userMapper::toResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    // Read User By ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID id) {

        User user = userService.getUserById(id);

        return ResponseEntity.ok(
                userMapper.toResponse(user)
        );
    }

    // Read User By Username
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(
            @PathVariable String username) {

        User user = userService.getUserByUsername(username);

        return ResponseEntity.ok(
                userMapper.toResponse(user)
        );
    }

    // Read User By Email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(
            @PathVariable String email) {

        User user = userService.getUserByEmail(email);

        return ResponseEntity.ok(
                userMapper.toResponse(user)
        );
    }

    // Read Users By Role
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(
            @PathVariable UserRole role) {

        List<UserResponse> users = userService
                .getUsersByRole(role)
                .stream()
                .map(userMapper::toResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    // Update User
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @RequestBody UserUpdateRequest request) {

        User user = userService.updateUser(id, request);

        return ResponseEntity.ok(
                userMapper.toResponse(user)
        );
    }

    // Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id) {

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}