package com.kingsman.FakeStoreAPIBack.DTO;

import com.kingsman.FakeStoreAPIBack.models.Category;

public record CategoryResponse(Long idCategory, String name, String description) {

    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(category.getIdCategory(), category.getName(), category.getDescription());
    }
}
