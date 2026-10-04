package com.kingsman.FakeStoreAPIBack.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kingsman.FakeStoreAPIBack.DTO.OrderItemView;
import com.kingsman.FakeStoreAPIBack.models.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Trae los ítems de una compra junto con el nombre del producto, en una sola consulta
    @Query(value = """
            SELECT oi.id_order_item AS idOrderItem, oi.id_product AS idProduct, p.name AS productName,
                   oi.quantity AS quantity, oi.unit_price AS unitPrice
            FROM order_items oi
            JOIN products p ON p.id_product = oi.id_product
            WHERE oi.id_order = :orderId
            """, nativeQuery = true)
    List<OrderItemView> findItemsByOrder(@Param("orderId") Long orderId);
}
