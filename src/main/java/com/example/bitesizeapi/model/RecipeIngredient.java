package com.example.bitesizeapi.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "recipeingredients")
@IdClass(RecipeIngredientId.class)
public class RecipeIngredient {

    @Id
    @Column(name = "recipeid")
    private Integer recipeId;

    @Id
    @Column(name = "ingredientid")
    private Integer ingredientId;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "unit")
    private String unit;

    // Not stored in RecipeIngredients table.
    // Used to return the ingredient name to Android.
    @Transient
    private String ingredientName;


    public RecipeIngredient() {
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


    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }


    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }
}