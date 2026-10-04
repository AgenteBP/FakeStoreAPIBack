package com.kingsman.FakeStoreAPIBack.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kingsman.FakeStoreAPIBack.DTO.InventoryView;
import com.kingsman.FakeStoreAPIBack.models.Inventory;

// Los UPDATE de stock se hacen directo en SQL en vez de leer la entidad, modificarla y guardarla.
// Así la base resta y suma en un solo paso, y si dos compras llegan al mismo tiempo no se pisan.
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProduct_IdProduct(Long productId);

    // Pasa stock de disponible a reservado solo si alcanza.
    // Devuelve las filas modificadas: 0 significa que no había stock suficiente.
    @Modifying
    @Query(value = """
            UPDATE inventory
            SET available_quantity = available_quantity - :quantity,
                reserved_quantity = reserved_quantity + :quantity,
                update_date = NOW()
            WHERE id_product = :productId AND available_quantity >= :quantity
            """, nativeQuery = true)
    int reserve(@Param("productId") Long productId, @Param("quantity") int quantity);

    // Compra pagada: lo reservado sale del inventario definitivamente
    @Modifying
    @Query(value = """
            UPDATE inventory
            SET reserved_quantity = reserved_quantity - :quantity,
                update_date = NOW()
            WHERE id_product = :productId
            """, nativeQuery = true)
    int confirmReserved(@Param("productId") Long productId, @Param("quantity") int quantity);

    // Compra cancelada antes de pagarse: lo reservado vuelve a disponible
    @Modifying
    @Query(value = """
            UPDATE inventory
            SET available_quantity = available_quantity + :quantity,
                reserved_quantity = reserved_quantity - :quantity,
                update_date = NOW()
            WHERE id_product = :productId
            """, nativeQuery = true)
    int releaseReserved(@Param("productId") Long productId, @Param("quantity") int quantity);

    // Compra cancelada después de pagarse: el stock ya se había descontado, se devuelve a disponible
    @Modifying
    @Query(value = """
            UPDATE inventory
            SET available_quantity = available_quantity + :quantity,
                update_date = NOW()
            WHERE id_product = :productId
            """, nativeQuery = true)
    int returnToAvailable(@Param("productId") Long productId, @Param("quantity") int quantity);

    // Reposición: suma unidades a lo disponible. Devuelve 0 si el producto no tiene fila de inventario.
    @Modifying
    @Query(value = """
            UPDATE inventory
            SET available_quantity = available_quantity + :quantity,
                update_date = NOW()
            WHERE id_product = :productId
            """, nativeQuery = true)
    int restock(@Param("productId") Long productId, @Param("quantity") int quantity);

    // Stock de todos los productos (también los desactivados) para la vista del ADMIN.
    // Primero los que tienen menos disponible, que son los que hay que reponer.
    @Query(value = """
            SELECT p.id_product AS idProduct, p.name AS productName, p.sku AS sku, p.active AS active,
                   COALESCE(i.available_quantity, 0) AS availableQuantity,
                   COALESCE(i.reserved_quantity, 0) AS reservedQuantity
            FROM products p
            LEFT JOIN inventory i ON i.id_product = p.id_product
            ORDER BY availableQuantity ASC, p.name ASC
            """, nativeQuery = true)
    List<InventoryView> findAllStock();
}
