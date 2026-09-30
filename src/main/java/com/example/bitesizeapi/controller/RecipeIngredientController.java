package com.example.bitesizeapi.controller;

import com.example.bitesizeapi.model.RecipeIngredient;
import com.example.bitesizeapi.model.RecipeIngredientId;
import com.example.bitesizeapi.repository.RecipeIngredientRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe-ingredients")
@CrossOrigin(origins = "*")
public class RecipeIngredientController {

    private final RecipeIngredientRepository recipeIngredientRepository;


    public RecipeIngredientController(
            RecipeIngredientRepository recipeIngredientRepository) {

        this.recipeIngredientRepository =
                recipeIngredientRepository;
    }


    // =====================================================
    // GET ALL RECIPE INGREDIENTS
    // =====================================================

    @GetMapping
    public List<RecipeIngredient> getAllRecipeIngredients() {

        return recipeIngredientRepository.findAll();
    }


    // =====================================================
    // GET INGREDIENTS FOR ONE RECIPE
    // =====================================================

    @GetMapping("/recipe/{recipeId}")
    public List<RecipeIngredient> getIngredientsByRecipe(
            @PathVariable Integer recipeId) {

        return recipeIngredientRepository
                .findByRecipeId(recipeId);
    }


    // =====================================================
    // ADD INGREDIENT TO RECIPE
    // =====================================================

    @PostMapping
    public RecipeIngredient createRecipeIngredient(
            @RequestBody RecipeIngredient recipeIngredient) {

        return recipeIngredientRepository.save(
                recipeIngredient
        );
    }


    // =====================================================
    // UPDATE RECIPE INGREDIENT
    // =====================================================

    @PutMapping("/{recipeId}/{ingredientId}")
    public ResponseEntity<RecipeIngredient> updateRecipeIngredient(
            @PathVariable Integer recipeId,
            @PathVariable Integer ingredientId,
            @RequestBody RecipeIngredient recipeIngredient) {

        RecipeIngredientId id =
                new RecipeIngredientId(
                        recipeId,
                        ingredientId
                );

        return recipeIngredientRepository
                .findById(id)
                .map(existing -> {

                    existing.setQuantity(
                            recipeIngredient.getQuantity()
                    );

                    existing.setUnit(
                            recipeIngredient.getUnit()
                    );

                    RecipeIngredient updated =
                            recipeIngredientRepository.save(
                                    existing
                            );

                    return ResponseEntity.ok(updated);

                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }


    // =====================================================
    // DELETE INGREDIENT FROM RECIPE
    // =====================================================

    @DeleteMapping("/{recipeId}/{ingredientId}")
    public ResponseEntity<Void> deleteRecipeIngredient(
            @PathVariable Integer recipeId,
            @PathVariable Integer ingredientId) {

        RecipeIngredientId id =
                new RecipeIngredientId(
                        recipeId,
                        ingredientId
                );


        if (!recipeIngredientRepository.existsById(id)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        recipeIngredientRepository.deleteById(id);


        return ResponseEntity
                .noContent()
                .build();
    }
}