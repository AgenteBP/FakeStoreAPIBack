package com.kingsman.FakeStoreAPIBack.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kingsman.FakeStoreAPIBack.models.PaymentMethod;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    @Query(value = "SELECT * FROM payment_methods WHERE id_order = :orderId ORDER BY creation_date", nativeQuery = true)
    List<PaymentMethod> findByOrderId(@Param("orderId") Long orderId);
}
