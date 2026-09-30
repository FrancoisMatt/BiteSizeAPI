package com.example.bitesizeapi.controller;

import com.example.bitesizeapi.model.AuditLog;
import com.example.bitesizeapi.model.User;
import com.example.bitesizeapi.model.LoginRequest;
import com.example.bitesizeapi.model.ResetPasswordRequest;

import com.example.bitesizeapi.repository.AuditLogRepository;
import com.example.bitesizeapi.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public UserController(
            UserRepository userRepository,
            AuditLogRepository auditLogRepository) {

        this.userRepository =
                userRepository;

        this.auditLogRepository =
                auditLogRepository;
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


        // Check duplicate email
        if (userRepository.existsByEmail(
                user.getEmail())) {

            return ResponseEntity
                    .badRequest()
                    .body("Email already exists");
        }


        // Database generates ID
        user.setUserId(null);


        User savedUser =
                userRepository.save(user);


        // =================================================
        // AUDIT CREATE
        // =================================================

        createAuditLog(
                savedUser.getUserId(),
                "CREATE",
                savedUser.getUserId(),
                "Created user account for " +
                        savedUser.getEmail()
        );


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


        // =================================================
        // AUDIT UPDATE
        // =================================================

        createAuditLog(
                savedUser.getUserId(),
                "UPDATE",
                savedUser.getUserId(),
                "Updated user profile for " +
                        savedUser.getEmail()
        );


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


        Optional<User> existingUser =
                userRepository.findById(id);


        if (existingUser.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        User user =
                existingUser.get();


        // Save details before deleting
        Integer userId =
                user.getUserId();

        String email =
                user.getEmail();


        // Delete user
        userRepository.deleteById(id);


        // =================================================
        // AUDIT DELETE
        // =================================================

        createAuditLog(
                userId,
                "DELETE",
                id,
                "Deleted user account for " + email
        );


        return ResponseEntity
                .noContent()
                .build();
    }


    // =====================================================
    // LOGIN
    // =====================================================

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
                    .body(
                            "Invalid email or password"
                    );
        }


        User user =
                userOptional.get();


        if (!user.getPasswordHash().equals(
                loginRequest.getPassword())) {

            return ResponseEntity
                    .status(401)
                    .body(
                            "Invalid email or password"
                    );
        }


        // We do NOT audit login
        return ResponseEntity.ok(
                user
        );
    }


    // =====================================================
    // RESET PASSWORD
    // =====================================================

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
                    .body(
                            "User not found"
                    );
        }


        User user =
                userOptional.get();


        user.setPasswordHash(
                request.getNewPassword()
        );


        User updatedUser =
                userRepository.save(user);


        // =================================================
        // AUDIT PASSWORD RESET
        // =================================================

        createAuditLog(
                updatedUser.getUserId(),
                "UPDATE",
                updatedUser.getUserId(),
                "Password reset for " +
                        updatedUser.getEmail()
        );


        return ResponseEntity.ok(
                updatedUser
        );
    }


    // =====================================================
    // CREATE AUDIT LOG
    // =====================================================

    private void createAuditLog(
            Integer userId,
            String action,
            Integer recordId,
            String description) {


        AuditLog auditLog =
                new AuditLog();


        auditLog.setUserId(
                userId
        );


        auditLog.setAction(
                action
        );


        auditLog.setTableName(
                "Users"
        );


        auditLog.setRecordId(
                recordId
        );


        auditLog.setDescription(
                description
        );


        auditLog.setCreatedAt(
                LocalDateTime.now()
        );


        auditLogRepository.save(
                auditLog
        );
    }
}