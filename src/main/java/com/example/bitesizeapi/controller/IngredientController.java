package com.example.bitesizeapi.controller;

import com.example.bitesizeapi.model.Ingredient;
import com.example.bitesizeapi.repository.IngredientRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin(origins = "*")
public class IngredientController {

    private final IngredientRepository ingredientRepository;

    public IngredientController(
            IngredientRepository ingredientRepository) {

        this.ingredientRepository = ingredientRepository;
    }


    // =====================================================
    // GET ALL INGREDIENTS
    // =====================================================

    @GetMapping
    public List<Ingredient> getAllIngredients() {

        return ingredientRepository.findAll();
    }


    // =====================================================
    // GET ONE INGREDIENT
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredient(
            @PathVariable Integer id) {

        Optional<Ingredient> ingredient =
                ingredientRepository.findById(id);

        if (ingredient.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(
                ingredient.get()
        );
    }
}