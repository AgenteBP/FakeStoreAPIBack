package com.kingsman.FakeStoreAPIBack.DTO;

import java.math.BigDecimal;

import com.kingsman.FakeStoreAPIBack.models.Product;

public record ProductResponse(Long idProduct, String name, String description, BigDecimal price, String sku,
        String imageUrl, Boolean active, Long idCategory, String categoryName, Integer availableQuantity) {

    public static ProductResponse fromView(ProductView view) {
        return new ProductResponse(view.getIdProduct(), view.getName(), view.getDescription(), view.getPrice(),
                view.getSku(), view.getImageUrl(), view.getActive(), view.getIdCategory(), view.getCategoryName(),
                view.getAvailableQuantity());
    }

    public static ProductResponse fromEntity(Product product, Integer availableQuantity) {
        return new ProductResponse(product.getIdProduct(), product.getName(), product.getDescription(),
                product.getPrice(), product.getSku(), product.getImageUrl(), product.getActive(),
                product.getCategory().getIdCategory(), product.getCategory().getName(), availableQuantity);
    }
}
