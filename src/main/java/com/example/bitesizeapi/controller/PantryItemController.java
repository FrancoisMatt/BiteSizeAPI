package com.example.bitesizeapi.controller;

import com.example.bitesizeapi.dto.PantryItemResponse;
import com.example.bitesizeapi.model.AuditLog;
import com.example.bitesizeapi.model.Ingredient;
import com.example.bitesizeapi.model.PantryItem;
import com.example.bitesizeapi.repository.AuditLogRepository;
import com.example.bitesizeapi.repository.IngredientRepository;
import com.example.bitesizeapi.repository.PantryItemRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/pantry")
@CrossOrigin(origins = "*")
public class PantryItemController {

    private final PantryItemRepository pantryItemRepository;
    private final IngredientRepository ingredientRepository;
    private final AuditLogRepository auditLogRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public PantryItemController(
            PantryItemRepository pantryItemRepository,
            IngredientRepository ingredientRepository,
            AuditLogRepository auditLogRepository) {

        this.pantryItemRepository =
                pantryItemRepository;

        this.ingredientRepository =
                ingredientRepository;

        this.auditLogRepository =
                auditLogRepository;
    }


    // =====================================================
    // GET ALL PANTRY ITEMS
    // =====================================================

    @GetMapping
    public List<PantryItem> getAllPantryItems() {

        return pantryItemRepository.findAll();
    }


    // =====================================================
    // GET PANTRY ITEMS FOR USER
    // =====================================================

    @GetMapping("/user/{userId}")
    public List<PantryItemResponse> getPantryByUser(
            @PathVariable Integer userId) {

        List<PantryItem> pantryItems =
                pantryItemRepository.findByUserId(userId);

        List<PantryItemResponse> response =
                new ArrayList<>();


        for (PantryItem pantryItem : pantryItems) {

            Optional<Ingredient> ingredient =
                    ingredientRepository.findById(
                            pantryItem.getIngredientId()
                    );


            String ingredientName =
                    ingredient
                            .map(Ingredient::getName)
                            .orElse("Unknown");


            response.add(
                    new PantryItemResponse(
                            pantryItem.getPantryItemId(),
                            pantryItem.getUserId(),
                            pantryItem.getIngredientId(),
                            ingredientName,
                            pantryItem.getQuantity(),
                            pantryItem.getUnit(),
                            pantryItem.getExpiryDate()
                    )
            );
        }


        return response;
    }


    // =====================================================
    // GET ONE PANTRY ITEM
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<PantryItem> getPantryItem(
            @PathVariable Integer id) {

        Optional<PantryItem> pantryItem =
                pantryItemRepository.findById(id);


        if (pantryItem.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        return ResponseEntity.ok(
                pantryItem.get()
        );
    }


    // =====================================================
    // CREATE PANTRY ITEM
    // =====================================================

    @PostMapping
    public ResponseEntity<?> createPantryItem(
            @RequestBody PantryItem pantryItem) {


        // =================================================
        // VALIDATE USER
        // =================================================

        if (pantryItem.getUserId() == null) {

            return ResponseEntity
                    .badRequest()
                    .body("User is required.");
        }


        // =================================================
        // VALIDATE INGREDIENT
        // =================================================

        if (pantryItem.getIngredientId() == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Ingredient is required.");
        }


        if (!ingredientRepository.existsById(
                pantryItem.getIngredientId())) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Selected ingredient does not exist."
                    );
        }


        // =================================================
        // VALIDATE QUANTITY
        // =================================================

        if (pantryItem.getQuantity() == null ||
                pantryItem.getQuantity().signum() <= 0) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Quantity must be greater than 0."
                    );
        }


        // =================================================
        // VALIDATE UNIT
        // =================================================

        if (pantryItem.getUnit() == null ||
                pantryItem.getUnit().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Unit is required.");
        }


        pantryItem.setUnit(
                pantryItem.getUnit().trim()
        );


        // =================================================
        // PREVENT DUPLICATE INGREDIENT
        // =================================================

        boolean ingredientAlreadyExists =
                pantryItemRepository
                        .existsByUserIdAndIngredientId(
                                pantryItem.getUserId(),
                                pantryItem.getIngredientId()
                        );


        if (ingredientAlreadyExists) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "This ingredient is already in your pantry. " +
                                    "Please edit the existing item instead."
                    );
        }


        // =================================================
        // CREATE PANTRY ITEM
        // =================================================

        pantryItem.setPantryItemId(null);


        PantryItem saved =
                pantryItemRepository.save(
                        pantryItem
                );


        // =================================================
        // GET INGREDIENT NAME FOR AUDIT
        // =================================================

        String ingredientName =
                ingredientRepository
                        .findById(
                                saved.getIngredientId()
                        )
                        .map(Ingredient::getName)
                        .orElse("Unknown");


        // =================================================
        // AUDIT CREATE
        // =================================================

        createAuditLog(
                saved.getUserId(),
                "CREATE",
                saved.getPantryItemId(),
                "Added " + ingredientName + " to pantry"
        );


        return ResponseEntity.ok(
                saved
        );
    }


    // =====================================================
    // UPDATE PANTRY ITEM
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePantryItem(
            @PathVariable Integer id,
            @RequestBody PantryItem updatedItem) {


        // =================================================
        // CHECK RECORD EXISTS
        // =================================================

        Optional<PantryItem> existing =
                pantryItemRepository.findById(id);


        if (existing.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        // =================================================
        // VALIDATE USER
        // =================================================

        if (updatedItem.getUserId() == null) {

            return ResponseEntity
                    .badRequest()
                    .body("User is required.");
        }


        // =================================================
        // VALIDATE INGREDIENT
        // =================================================

        if (updatedItem.getIngredientId() == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Ingredient is required.");
        }


        if (!ingredientRepository.existsById(
                updatedItem.getIngredientId())) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Selected ingredient does not exist."
                    );
        }


        // =================================================
        // VALIDATE QUANTITY
        // =================================================

        if (updatedItem.getQuantity() == null ||
                updatedItem.getQuantity().signum() <= 0) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Quantity must be greater than 0."
                    );
        }


        // =================================================
        // VALIDATE UNIT
        // =================================================

        if (updatedItem.getUnit() == null ||
                updatedItem.getUnit().trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Unit is required.");
        }


        // =================================================
        // PREVENT DUPLICATES DURING UPDATE
        // =================================================

        boolean duplicateIngredient =
                pantryItemRepository
                        .existsByUserIdAndIngredientIdAndPantryItemIdNot(
                                updatedItem.getUserId(),
                                updatedItem.getIngredientId(),
                                id
                        );


        if (duplicateIngredient) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "This ingredient is already in your pantry. " +
                                    "Please edit the existing item instead."
                    );
        }


        // =================================================
        // UPDATE PANTRY ITEM
        // =================================================

        PantryItem pantryItem =
                existing.get();


        pantryItem.setUserId(
                updatedItem.getUserId()
        );


        pantryItem.setIngredientId(
                updatedItem.getIngredientId()
        );


        pantryItem.setQuantity(
                updatedItem.getQuantity()
        );


        pantryItem.setUnit(
                updatedItem.getUnit().trim()
        );


        pantryItem.setExpiryDate(
                updatedItem.getExpiryDate()
        );


        PantryItem saved =
                pantryItemRepository.save(
                        pantryItem
                );


        // =================================================
        // GET INGREDIENT NAME FOR AUDIT
        // =================================================

        String ingredientName =
                ingredientRepository
                        .findById(
                                saved.getIngredientId()
                        )
                        .map(Ingredient::getName)
                        .orElse("Unknown");


        // =================================================
        // AUDIT UPDATE
        // =================================================

        createAuditLog(
                saved.getUserId(),
                "UPDATE",
                saved.getPantryItemId(),
                "Updated " + ingredientName + " in pantry"
        );


        return ResponseEntity.ok(
                saved
        );
    }


    // =====================================================
    // DELETE PANTRY ITEM
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePantryItem(
            @PathVariable Integer id) {


        // =================================================
        // GET ITEM BEFORE DELETE
        // =================================================

        Optional<PantryItem> existing =
                pantryItemRepository.findById(id);


        if (existing.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        PantryItem pantryItem =
                existing.get();


        // Save these before deleting
        Integer userId =
                pantryItem.getUserId();

        Integer ingredientId =
                pantryItem.getIngredientId();


        // =================================================
        // GET INGREDIENT NAME
        // =================================================

        String ingredientName =
                ingredientRepository
                        .findById(ingredientId)
                        .map(Ingredient::getName)
                        .orElse("Unknown");


        // =================================================
        // DELETE
        // =================================================

        pantryItemRepository.deleteById(id);


        // =================================================
        // AUDIT DELETE
        // =================================================

        createAuditLog(
                userId,
                "DELETE",
                id,
                "Deleted " + ingredientName + " from pantry"
        );


        return ResponseEntity
                .noContent()
                .build();
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
                "PantryItems"
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