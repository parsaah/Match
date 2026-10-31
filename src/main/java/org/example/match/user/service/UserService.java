package org.example.match.user.service;

import org.example.match.user.dto.AdminUserUpdateRequest;
import org.example.match.user.dto.UserCreateRequest;
import org.example.match.user.dto.UserUpdateRequest;
import org.example.match.user.entity.User;
import org.example.match.user.enums.UserRole;
import org.example.match.user.exception.EmailAlreadyExistsException;
import org.example.match.user.exception.UserNotFoundException;
import org.example.match.user.exception.UsernameAlreadyExistsException;
import org.example.match.user.mapper.UserMapper;
import org.example.match.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public User createUser(UserCreateRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException(
                    request.getUsername()
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    request.getEmail()
            );
        }

        User user = userMapper.toEntity(request);

        return userRepository.save(user);
    }

    public User updateUser(
            UUID id,
            UserUpdateRequest request) {

        User existingUser = getUserById(id);

        if (!existingUser.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new EmailAlreadyExistsException(
                    request.getEmail()
            );
        }

        userMapper.updateEntity(request, existingUser);

        return userRepository.save(existingUser);
    }

    public User updateUserByAdmin(
            UUID id,
            AdminUserUpdateRequest request) {

        User existingUser = getUserById(id);

        if (!existingUser.getUsername().equals(request.getUsername())
                && userRepository.existsByUsername(request.getUsername())) {

            throw new UsernameAlreadyExistsException(
                    request.getUsername()
            );
        }

        if (!existingUser.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {

            throw new EmailAlreadyExistsException(
                    request.getEmail()
            );
        }

        userMapper.updateEntityByAdmin(
                request,
                existingUser
        );

        return userRepository.save(existingUser);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException(id)
                );
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Read - By Username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    // Read - By Email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    // Read - By Role
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    public void deleteUser(UUID id) {

        User user = getUserById(id);

        userRepository.delete(user);
    }
}
