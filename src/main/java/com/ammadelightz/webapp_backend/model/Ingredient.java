package com.ammadelightz.webapp_backend.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    private String name;
    private Object quantity;
    private String unit;
    private String notes;
}
