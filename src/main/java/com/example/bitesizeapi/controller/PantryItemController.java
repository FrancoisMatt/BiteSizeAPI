package com.example.bitesizeapi.controller;

import com.example.bitesizeapi.dto.PantryItemResponse;
import com.example.bitesizeapi.model.Ingredient;
import com.example.bitesizeapi.model.PantryItem;
import com.example.bitesizeapi.repository.IngredientRepository;
import com.example.bitesizeapi.repository.PantryItemRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pantry")
@CrossOrigin(origins = "*")
public class PantryItemController {

    private final PantryItemRepository pantryItemRepository;
    private final IngredientRepository ingredientRepository;


    // Constructor
    public PantryItemController(
            PantryItemRepository pantryItemRepository,
            IngredientRepository ingredientRepository) {

        this.pantryItemRepository = pantryItemRepository;
        this.ingredientRepository = ingredientRepository;
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
    // Includes Ingredient Name
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
    // CREATE
    // =====================================================

    @PostMapping
    public PantryItem createPantryItem(
            @RequestBody PantryItem pantryItem) {

        // Database generates the ID
        pantryItem.setPantryItemId(null);

        return pantryItemRepository.save(
                pantryItem
        );
    }


    // =====================================================
    // UPDATE
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<PantryItem> updatePantryItem(
            @PathVariable Integer id,
            @RequestBody PantryItem updatedItem) {

        Optional<PantryItem> existing =
                pantryItemRepository.findById(id);


        if (existing.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


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
                updatedItem.getUnit()
        );

        pantryItem.setExpiryDate(
                updatedItem.getExpiryDate()
        );


        PantryItem saved =
                pantryItemRepository.save(
                        pantryItem
                );


        return ResponseEntity.ok(saved);
    }


    // =====================================================
    // DELETE
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePantryItem(
            @PathVariable Integer id) {

        if (!pantryItemRepository.existsById(id)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        pantryItemRepository.deleteById(id);


        return ResponseEntity
                .noContent()
                .build();
    }
}