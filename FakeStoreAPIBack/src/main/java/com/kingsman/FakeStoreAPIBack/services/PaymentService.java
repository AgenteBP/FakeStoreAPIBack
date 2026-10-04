package com.kingsman.FakeStoreAPIBack.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kingsman.FakeStoreAPIBack.DTO.PaymentRequest;
import com.kingsman.FakeStoreAPIBack.DTO.PaymentResponse;
import com.kingsman.FakeStoreAPIBack.exception.NotFoundException;
import com.kingsman.FakeStoreAPIBack.models.Order;
import com.kingsman.FakeStoreAPIBack.models.PaymentMethod;
import com.kingsman.FakeStoreAPIBack.repositories.OrderRepository;
import com.kingsman.FakeStoreAPIBack.repositories.PaymentMethodRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final OrderRepository orderRepository;
    private final CurrentUserService currentUserService;

    // Pagos de una compra en el orden en que se registraron (puede haber intentos rechazados).
    // Un CLIENT solo puede ver los pagos de sus propias compras.
    @Transactional(readOnly = true)
    public List<PaymentResponse> getByOrder(Long orderId) {
        findOrder(orderId);
        return paymentMethodRepository.findByOrderId(orderId).stream().map(PaymentResponse::fromEntity).toList();
    }

    // Registra un pago para la compra. Un CLIENT solo puede pagar sus propias compras.
    // No cambia el estado de la compra: eso se hace aparte con PATCH /api/orders/{id}/status.
    // TODO: pasar type y transactionStatus a enum cuando estén definidos los valores posibles.
    @Transactional
    public PaymentResponse create(Long orderId, PaymentRequest request) {
        RequestValidator.requireText(request.type(), "type");
        RequestValidator.requireText(request.transactionStatus(), "transactionStatus");

        Order order = findOrder(orderId);

        PaymentMethod payment = new PaymentMethod();
        payment.setOrder(order);
        payment.setType(request.type());
        payment.setTransactionStatus(request.transactionStatus());
        payment.setExternalReference(request.externalReference());

        return PaymentResponse.fromEntity(paymentMethodRepository.save(payment));
    }

    // Busca la compra y verifica que sea del usuario logueado (o que sea ADMIN)
    private Order findOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("No existe la compra con id " + orderId));
        currentUserService.checkOwnerOrAdmin(order.getUser().getIdUser());
        return order;
    }
}
