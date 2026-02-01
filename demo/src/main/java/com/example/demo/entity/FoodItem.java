package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double defaultPrice;
    private String icon; // Store emoji or icon class

    @Column(columnDefinition = "boolean default true")
    private boolean isDefault = true; // Default items appear on all new days

    public FoodItem() {
    }

    public FoodItem(String name, Double defaultPrice, String icon) {
        this.name = name;
        this.defaultPrice = defaultPrice;
        this.icon = icon;
        this.isDefault = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(Double defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
