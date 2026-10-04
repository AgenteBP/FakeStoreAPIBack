package com.kingsman.FakeStoreAPIBack.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kingsman.FakeStoreAPIBack.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT * FROM orders ORDER BY order_date DESC", nativeQuery = true)
    List<Order> findAllNewestFirst();

    @Query(value = "SELECT * FROM orders WHERE id_user = :userId ORDER BY order_date DESC", nativeQuery = true)
    List<Order> findByUserNewestFirst(@Param("userId") Long userId);

    @Query(value = "SELECT COUNT(*) FROM orders WHERE id_user = :userId", nativeQuery = true)
    long countByUser(@Param("userId") Long userId);

    @Query(value = "SELECT COUNT(*) FROM orders WHERE id_shipping_address = :addressId", nativeQuery = true)
    long countByShippingAddress(@Param("addressId") Long addressId);

    // Trae la compra y bloquea su fila hasta que termine la transacción (FOR UPDATE).
    // Se usa en todo cambio de estado: si el cliente toca "Pagar" justo cuando la tarea automática
    // también la paga, la segunda espera, ve que ya no está PENDING y no mueve el stock dos veces.
    @Query(value = "SELECT * FROM orders WHERE id_order = :id FOR UPDATE", nativeQuery = true)
    Optional<Order> findByIdForUpdate(@Param("id") Long id);

    // Pago simulado: ids de las compras que siguen PENDING desde antes de cutoff
    @Query(value = "SELECT id_order FROM orders WHERE status = 'PENDING' AND order_date <= :cutoff",
            nativeQuery = true)
    List<Long> findPendingIdsBefore(@Param("cutoff") LocalDateTime cutoff);

    // Baja de usuario: ids de sus compras que todavía no salieron (reservadas o pagadas)
    @Query(value = "SELECT id_order FROM orders WHERE id_user = :userId AND status IN ('PENDING', 'PAID')",
            nativeQuery = true)
    List<Long> findNotShippedIdsByUser(@Param("userId") Long userId);

    // Envío simulado: un solo UPDATE marca como SHIPPED todas las compras PAID desde antes de cutoff
    @Modifying
    @Query(value = """
            UPDATE orders
            SET status = 'SHIPPED', update_date = :now
            WHERE status = 'PAID' AND update_date <= :cutoff
            """, nativeQuery = true)
    int shipPaidOrdersBefore(@Param("cutoff") LocalDateTime cutoff, @Param("now") LocalDateTime now);

    // Entrega simulada: marca como DELIVERED las compras SHIPPED desde antes de cutoff
    @Modifying
    @Query(value = """
            UPDATE orders
            SET status = 'DELIVERED', update_date = :now
            WHERE status = 'SHIPPED' AND update_date <= :cutoff
            """, nativeQuery = true)
    int deliverShippedOrdersBefore(@Param("cutoff") LocalDateTime cutoff, @Param("now") LocalDateTime now);
}
