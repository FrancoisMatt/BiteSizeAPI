package com.example.bitesizeapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PantryItemResponse {

    private Integer pantryItemId;
    private Integer userId;
    private Integer ingredientId;

    private String name;

    private BigDecimal quantity;
    private String unit;
    private LocalDate expiryDate;


    public PantryItemResponse(
            Integer pantryItemId,
            Integer userId,
            Integer ingredientId,
            String name,
            BigDecimal quantity,
            String unit,
            LocalDate expiryDate) {

        this.pantryItemId = pantryItemId;
        this.userId = userId;
        this.ingredientId = ingredientId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }


    public Integer getPantryItemId() {
        return pantryItemId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getIngredientId() {
        return ingredientId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }
}