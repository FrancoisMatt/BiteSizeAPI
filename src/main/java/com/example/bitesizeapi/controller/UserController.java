package com.example.bitesizeapi.controller;

import com.example.bitesizeapi.model.User;
import com.example.bitesizeapi.repository.UserRepository;
import com.example.bitesizeapi.model.LoginRequest;
import com.example.bitesizeapi.model.ResetPasswordRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;


    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // =====================================================
    // GET ALL USERS
    // =====================================================

    @GetMapping
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    // =====================================================
    // GET USER BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable Integer id) {

        Optional<User> user =
                userRepository.findById(id);

        if (user.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(
                user.get()
        );
    }


    // =====================================================
    // CREATE USER
    // =====================================================

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody User user) {

        if (userRepository.existsByEmail(
                user.getEmail())) {

            return ResponseEntity
                    .badRequest()
                    .body("Email already exists");
        }


        user.setUserId(null);

        User savedUser =
                userRepository.save(user);

        return ResponseEntity.ok(
                savedUser
        );
    }


    // =====================================================
    // UPDATE USER
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Integer id,
            @RequestBody User updatedUser) {

        Optional<User> existingUser =
                userRepository.findById(id);


        if (existingUser.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        User user =
                existingUser.get();


        user.setFirstName(
                updatedUser.getFirstName()
        );

        user.setLastName(
                updatedUser.getLastName()
        );

        user.setEmail(
                updatedUser.getEmail()
        );

        user.setPasswordHash(
                updatedUser.getPasswordHash()
        );

        user.setPushNotifications(
                updatedUser.isPushNotifications()
        );

        user.setExpiryNotifications(
                updatedUser.isExpiryNotifications()
        );

        user.setExpiryNotificationDays(
                updatedUser.getExpiryNotificationDays()
        );

        user.setDarkMode(
                updatedUser.isDarkMode()
        );


        User savedUser =
                userRepository.save(user);


        return ResponseEntity.ok(
                savedUser
        );
    }


    // =====================================================
    // DELETE USER
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Integer id) {


        if (!userRepository.existsById(id)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        userRepository.deleteById(id);


        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest) {

        Optional<User> userOptional =
                userRepository.findByEmail(
                        loginRequest.getEmail()
                );

        if (userOptional.isEmpty()) {

            return ResponseEntity
                    .status(401)
                    .body("Invalid email or password");
        }


        User user = userOptional.get();


        if (!user.getPasswordHash().equals(
                loginRequest.getPassword())) {

            return ResponseEntity
                    .status(401)
                    .body("Invalid email or password");
        }


        return ResponseEntity.ok(user);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        Optional<User> userOptional =
                userRepository.findByEmail(
                        request.getEmail()
                );


        if (userOptional.isEmpty()) {

            return ResponseEntity
                    .status(404)
                    .body("User not found");
        }


        User user =
                userOptional.get();


        user.setPasswordHash(
                request.getNewPassword()
        );


        User updatedUser =
                userRepository.save(user);


        return ResponseEntity.ok(
                updatedUser
        );
    }
}