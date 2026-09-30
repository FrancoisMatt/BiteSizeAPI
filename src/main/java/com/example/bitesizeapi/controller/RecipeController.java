package com.example.bitesizeapi.controller;

import com.example.bitesizeapi.model.PantryItem;
import com.example.bitesizeapi.model.Recipe;
import com.example.bitesizeapi.model.RecipeIngredient;

import com.example.bitesizeapi.repository.PantryItemRepository;
import com.example.bitesizeapi.repository.RecipeIngredientRepository;
import com.example.bitesizeapi.repository.RecipeRepository;
import com.example.bitesizeapi.repository.IngredientRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*")
public class RecipeController {

    private final RecipeRepository recipeRepository;
    private final PantryItemRepository pantryItemRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public RecipeController(
            RecipeRepository recipeRepository,
            PantryItemRepository pantryItemRepository,
            RecipeIngredientRepository recipeIngredientRepository,
            IngredientRepository ingredientRepository) {

        this.recipeRepository = recipeRepository;
        this.pantryItemRepository = pantryItemRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
    }


    // =====================================================
    // GET ALL RECIPES
    // =====================================================

    @GetMapping
    public List<Recipe> getAllRecipes() {

        return recipeRepository.findAll();
    }


    // =====================================================
    // GET SUGGESTED RECIPES FOR USER
    // =====================================================

    @GetMapping("/suggested/{userId}")
    public List<Recipe> getSuggestedRecipes(
            @PathVariable Integer userId) {


        List<PantryItem> pantryItems =
                pantryItemRepository.findByUserId(userId);


        List<Recipe> recipes =
                recipeRepository.findAll();


        List<Recipe> suggestedRecipes =
                new ArrayList<>();


        // Check every recipe
        for (Recipe recipe : recipes) {


            List<RecipeIngredient> requiredIngredients =
                    recipeIngredientRepository
                            .findByRecipeId(
                                    recipe.getRecipeId()
                            );


            // Ignore recipes with no ingredients
            if (requiredIngredients.isEmpty()) {

                continue;
            }


            boolean userHasAllIngredients = true;


            // =================================================
            // CHECK EACH REQUIRED INGREDIENT
            // =================================================

            for (RecipeIngredient requiredIngredient
                    : requiredIngredients) {


                boolean ingredientAvailable = false;


                // Check pantry
                for (PantryItem pantryItem : pantryItems) {


                    // =========================================
                    // CHECK INGREDIENT
                    // =========================================

                    if (!pantryItem
                            .getIngredientId()
                            .equals(
                                    requiredIngredient
                                            .getIngredientId()
                            )) {

                        continue;
                    }


                    // =========================================
                    // CHECK VALUES
                    // =========================================

                    if (pantryItem.getQuantity() == null ||
                            requiredIngredient.getQuantity() == null ||
                            pantryItem.getUnit() == null ||
                            requiredIngredient.getUnit() == null) {

                        continue;
                    }


                    // =========================================
                    // CHECK UNIT TYPES ARE COMPATIBLE
                    // =========================================

                    if (!unitsCompatible(
                            pantryItem.getUnit(),
                            requiredIngredient.getUnit())) {

                        continue;
                    }


                    // =========================================
                    // CONVERT QUANTITIES
                    // =========================================

                    BigDecimal pantryQuantity =
                            convertToBaseUnit(
                                    pantryItem.getQuantity(),
                                    pantryItem.getUnit()
                            );


                    BigDecimal requiredQuantity =
                            convertToBaseUnit(
                                    requiredIngredient.getQuantity(),
                                    requiredIngredient.getUnit()
                            );


                    // =========================================
                    // CHECK ENOUGH QUANTITY
                    // =========================================

                    if (pantryQuantity.compareTo(
                            requiredQuantity) >= 0) {


                        ingredientAvailable = true;

                        break;
                    }
                }


                // One ingredient is missing or insufficient
                if (!ingredientAvailable) {

                    userHasAllIngredients = false;

                    break;
                }
            }


            // =================================================
            // USER CAN MAKE RECIPE
            // =================================================

            if (userHasAllIngredients) {


                // Add ingredient names
                for (RecipeIngredient requiredIngredient
                        : requiredIngredients) {


                    ingredientRepository
                            .findById(
                                    requiredIngredient
                                            .getIngredientId()
                            )
                            .ifPresent(ingredient -> {

                                requiredIngredient
                                        .setIngredientName(
                                                ingredient.getName()
                                        );
                            });
                }


                recipe.setIngredients(
                        requiredIngredients
                );


                suggestedRecipes.add(
                        recipe
                );
            }
        }


        return suggestedRecipes;
    }


    // =====================================================
    // CHECK IF UNITS ARE COMPATIBLE
    // =====================================================

    private boolean unitsCompatible(
            String pantryUnit,
            String recipeUnit) {


        String pantry =
                pantryUnit.trim().toLowerCase();


        String recipe =
                recipeUnit.trim().toLowerCase();


        // Weight
        boolean pantryWeight =
                pantry.equals("g") ||
                        pantry.equals("kg");

        boolean recipeWeight =
                recipe.equals("g") ||
                        recipe.equals("kg");


        if (pantryWeight && recipeWeight) {

            return true;
        }


        // Volume
        boolean pantryVolume =
                pantry.equals("ml") ||
                        pantry.equals("l");

        boolean recipeVolume =
                recipe.equals("ml") ||
                        recipe.equals("l");


        if (pantryVolume && recipeVolume) {

            return true;
        }


        // Individual units
        boolean pantryUnits =
                pantry.equals("unit") ||
                        pantry.equals("units");

        boolean recipeUnits =
                recipe.equals("unit") ||
                        recipe.equals("units");


        return pantryUnits && recipeUnits;
    }


    // =====================================================
    // CONVERT TO BASE UNIT
    //
    // kg -> g
    // L  -> ml
    // Units stay Units
    // =====================================================

    private BigDecimal convertToBaseUnit(
            BigDecimal quantity,
            String unit) {


        String cleanUnit =
                unit.trim().toLowerCase();


        switch (cleanUnit) {

            case "kg":

                return quantity.multiply(
                        BigDecimal.valueOf(1000)
                );


            case "g":

                return quantity;


            case "l":

                return quantity.multiply(
                        BigDecimal.valueOf(1000)
                );


            case "ml":

                return quantity;


            case "unit":

            case "units":

                return quantity;


            default:

                return quantity;
        }
    }


    // =====================================================
    // GET RECIPE BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(
            @PathVariable Integer id) {


        Optional<Recipe> recipe =
                recipeRepository.findById(id);


        if (recipe.isPresent()) {

            return ResponseEntity.ok(
                    recipe.get()
            );
        }


        return ResponseEntity
                .notFound()
                .build();
    }


    // =====================================================
    // GET INGREDIENTS FOR RECIPE
    // =====================================================

    @GetMapping("/{recipeId}/ingredients")
    public List<RecipeIngredient> getRecipeIngredients(
            @PathVariable Integer recipeId) {


        List<RecipeIngredient> recipeIngredients =
                recipeIngredientRepository
                        .findByRecipeId(recipeId);


        for (RecipeIngredient recipeIngredient
                : recipeIngredients) {


            ingredientRepository
                    .findById(
                            recipeIngredient.getIngredientId()
                    )
                    .ifPresent(ingredient -> {

                        recipeIngredient
                                .setIngredientName(
                                        ingredient.getName()
                                );
                    });
        }


        return recipeIngredients;
    }


    // =====================================================
    // CREATE RECIPE
    // =====================================================

    @PostMapping
    public Recipe createRecipe(
            @RequestBody Recipe recipe) {


        recipe.setRecipeId(null);


        return recipeRepository.save(
                recipe
        );
    }


    // =====================================================
    // UPDATE RECIPE
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable Integer id,
            @RequestBody Recipe recipe) {


        Optional<Recipe> existingRecipe =
                recipeRepository.findById(id);


        if (existingRecipe.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        Recipe existing =
                existingRecipe.get();


        existing.setName(
                recipe.getName()
        );


        existing.setDescription(
                recipe.getDescription()
        );


        existing.setInstructions(
                recipe.getInstructions()
        );


        existing.setPrepTimeMinutes(
                recipe.getPrepTimeMinutes()
        );


        existing.setServings(
                recipe.getServings()
        );


        Recipe updated =
                recipeRepository.save(
                        existing
                );


        return ResponseEntity.ok(
                updated
        );
    }


    // =====================================================
    // DELETE RECIPE
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Integer id) {


        if (!recipeRepository.existsById(id)) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        recipeRepository.deleteById(id);


        return ResponseEntity
                .noContent()
                .build();
    }
}