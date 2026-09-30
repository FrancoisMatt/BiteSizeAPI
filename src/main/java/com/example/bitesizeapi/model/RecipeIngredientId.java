package com.example.bitesizeapi.model;

import java.io.Serializable;
import java.util.Objects;

public class RecipeIngredientId implements Serializable {

    private Integer recipeId;
    private Integer ingredientId;

    public RecipeIngredientId() {
    }

    public RecipeIngredientId(Integer recipeId, Integer ingredientId) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    public Integer getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Integer ingredientId) {
        this.ingredientId = ingredientId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof RecipeIngredientId)) {
            return false;
        }

        RecipeIngredientId that =
                (RecipeIngredientId) o;

        return Objects.equals(recipeId, that.recipeId)
                && Objects.equals(ingredientId, that.ingredientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                recipeId,
                ingredientId
        );
    }
}