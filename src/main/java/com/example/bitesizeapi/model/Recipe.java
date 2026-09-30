package com.example.bitesizeapi.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name = "recipeid")
    private Integer recipeId;


    @Column(
            name = "name",
            nullable = false
    )
    private String name;


    @Column(name = "description")
    private String description;


    @Column(
            name = "instructions",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String instructions;


    @Column(name = "preptimeminutes")
    private Integer prepTimeMinutes;


    @Column(name = "servings")
    private Integer servings;


    @Column(
            name = "createdat",
            nullable = false
    )
    private LocalDateTime createdAt;


    @Column(
            name = "updatedat",
            nullable = false
    )
    private LocalDateTime updatedAt;


    // =====================================================
    // RECIPE INGREDIENTS
    // Not a column in the Recipes table
    // Used for API responses
    // =====================================================

    @Transient
    private List<RecipeIngredient> ingredients;


    public Recipe() {
    }


    // =====================================================
    // CREATE / UPDATE DATES
    // =====================================================

    @PrePersist
    protected void onCreate() {

        createdAt =
                LocalDateTime.now();

        updatedAt =
                LocalDateTime.now();
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getRecipeId() {
        return recipeId;
    }


    public void setRecipeId(
            Integer recipeId) {

        this.recipeId = recipeId;
    }


    public String getName() {
        return name;
    }


    public void setName(
            String name) {

        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(
            String description) {

        this.description = description;
    }


    public String getInstructions() {
        return instructions;
    }


    public void setInstructions(
            String instructions) {

        this.instructions = instructions;
    }


    public Integer getPrepTimeMinutes() {
        return prepTimeMinutes;
    }


    public void setPrepTimeMinutes(
            Integer prepTimeMinutes) {

        this.prepTimeMinutes =
                prepTimeMinutes;
    }


    public Integer getServings() {
        return servings;
    }


    public void setServings(
            Integer servings) {

        this.servings = servings;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }


    // =====================================================
    // INGREDIENTS
    // =====================================================

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }


    public void setIngredients(
            List<RecipeIngredient> ingredients) {

        this.ingredients = ingredients;
    }
}
