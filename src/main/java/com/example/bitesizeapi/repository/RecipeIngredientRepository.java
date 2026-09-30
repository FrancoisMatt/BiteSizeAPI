package com.example.bitesizeapi.repository;

import com.example.bitesizeapi.model.RecipeIngredient;
import com.example.bitesizeapi.model.RecipeIngredientId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientRepository
        extends JpaRepository<RecipeIngredient, RecipeIngredientId> {

    List<RecipeIngredient> findByRecipeId(Integer recipeId);

    List<RecipeIngredient> findByIngredientId(Integer ingredientId);
}