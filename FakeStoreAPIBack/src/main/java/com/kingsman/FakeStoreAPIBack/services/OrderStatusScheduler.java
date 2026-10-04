package com.kingsman.FakeStoreAPIBack.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Tarea automática que simula el avance de las compras. Cada 15 segundos:
//   PENDING -> PAID       después de orders.payment-delay-minutes  (pago simulado)
//   PAID    -> SHIPPED    después de orders.shipping-delay-minutes (envío simulado)
//   SHIPPED -> DELIVERED  después de orders.delivery-delay-minutes (entrega simulada)
// El ADMIN también puede hacer estos cambios a mano. Se puede reemplazar por la acción
// de un vendedor cuando se agregue ese rol.
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusScheduler {

    private final OrderService orderService;

    // Cada 15 segundos para que los tiempos de pocos minutos se cumplan con poco desfase
    @Scheduled(fixedDelay = 15_000)
    public void advanceOrders() {
        payPendingOrders();

        int shipped = orderService.shipPaidOrders();
        if (shipped > 0) {
            log.info("Envío simulado: {} compra(s) pasaron de PAID a SHIPPED", shipped);
        }

        int delivered = orderService.deliverShippedOrders();
        if (delivered > 0) {
            log.info("Entrega simulada: {} compra(s) pasaron de SHIPPED a DELIVERED", delivered);
        }
    }

    // El pago va de a una compra porque mueve stock de cada ítem y registra un pago.
    // Si una falla, se registra el error y se sigue con las demás.
    private void payPendingOrders() {
        for (Long orderId : orderService.findOrdersToAutoPay()) {
            try {
                if (orderService.autoPay(orderId)) {
                    log.info("Pago simulado: la compra {} pasó de PENDING a PAID", orderId);
                }
            } catch (RuntimeException e) {
                log.error("No se pudo pagar automáticamente la compra {}", orderId, e);
            }
        }
    }
}
