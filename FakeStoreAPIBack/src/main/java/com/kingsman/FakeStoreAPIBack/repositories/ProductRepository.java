package com.kingsman.FakeStoreAPIBack.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kingsman.FakeStoreAPIBack.DTO.ProductView;
import com.kingsman.FakeStoreAPIBack.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // SELECT compartido por las consultas de abajo: trae el producto con el nombre de su categoría
    // y el stock disponible en una sola consulta. LEFT JOIN para que un producto sin fila de
    // inventario aparezca igual, con stock 0.
    String PRODUCT_SELECT = """
            SELECT p.id_product AS idProduct, p.name AS name, p.description AS description,
                   p.price AS price, p.sku AS sku, p.image_url AS imageUrl, p.active AS active,
                   c.id_category AS idCategory, c.name AS categoryName,
                   COALESCE(i.available_quantity, 0) AS availableQuantity
            FROM products p
            JOIN categories c ON c.id_category = p.id_category
            LEFT JOIN inventory i ON i.id_product = p.id_product
            """;

    @Query(value = PRODUCT_SELECT + " WHERE p.active = true ORDER BY p.name", nativeQuery = true)
    List<ProductView> findAllActive();

    @Query(value = PRODUCT_SELECT + " WHERE p.active = true AND p.id_category = :categoryId ORDER BY p.name",
            nativeQuery = true)
    List<ProductView> findActiveByCategory(@Param("categoryId") Long categoryId);

    // Sin filtrar por active: un producto desactivado se puede seguir consultando por id
    @Query(value = PRODUCT_SELECT + " WHERE p.id_product = :productId", nativeQuery = true)
    Optional<ProductView> findViewById(@Param("productId") Long productId);

    boolean existsBySku(String sku);

    boolean existsByCategory_IdCategory(Long categoryId);
}
