package com.kingsman.FakeStoreAPIBack.DTO;

import java.math.BigDecimal;

// Proyección para las consultas nativas de ProductRepository.
// Spring llena cada getter con la columna del SELECT que tiene el mismo alias (ej. "AS categoryName").
public interface ProductView {
    Long getIdProduct();

    String getName();

    String getDescription();

    BigDecimal getPrice();

    String getSku();

    String getImageUrl();

    Boolean getActive();

    Long getIdCategory();

    String getCategoryName();

    Integer getAvailableQuantity();
}
