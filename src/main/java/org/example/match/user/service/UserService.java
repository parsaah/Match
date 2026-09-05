package org.example.match.user.service;

import org.example.match.user.entity.User;
import org.example.match.user.enums.UserRole;
import org.example.match.user.exception.EmailAlreadyExistsException;
import org.example.match.user.exception.UserNotFoundException;
import org.example.match.user.exception.UsernameAlreadyExistsException;
import org.example.match.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    // Create
    public User createUser(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistsException(user.getUsername());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        return userRepository.save(user);
    }

    // Read - All
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Read - By ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + id));
    }

    // Read - By Username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with username: " + username));
    }

    // Read - By Email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with email: " + email));
    }

    // Read - By Role
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    // Update
    public User updateUser(Long id, User user) {

        User existingUser = getUserById(id);

        // username تغییر کرده؟
        if (!existingUser.getUsername().equals(user.getUsername())) {

            if (userRepository.existsByUsername(user.getUsername())) {
                throw new UsernameAlreadyExistsException(
                        user.getUsername()
                );
            }

            existingUser.setUsername(user.getUsername());
        }

        // email تغییر کرده؟
        if (!existingUser.getEmail().equals(user.getEmail())) {

            if (userRepository.existsByEmail(user.getEmail())) {
                throw new EmailAlreadyExistsException(
                        user.getEmail()
                );
            }

            existingUser.setEmail(user.getEmail());
        }

//        existingUser.setUsername(user.getUsername());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setRole(user.getRole());
        existingUser.setStatus(user.getStatus());
        existingUser.setAge(user.getAge());
        existingUser.setPhoneNumber(user.getPhoneNumber());
//        existingUser.setEmail(user.getEmail());

        return userRepository.save(existingUser);
    }

    // Delete
    public void deleteUser(Long id) {

        User user = getUserById(id);
        userRepository.delete(user);
    }
}
