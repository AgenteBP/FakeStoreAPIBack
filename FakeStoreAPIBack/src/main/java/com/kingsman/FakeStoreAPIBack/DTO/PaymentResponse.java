package com.kingsman.FakeStoreAPIBack.DTO;

import java.time.LocalDateTime;

import com.kingsman.FakeStoreAPIBack.models.PaymentMethod;

public record PaymentResponse(Long idPaymentMethod, Long idOrder, String type, String transactionStatus,
        String externalReference, LocalDateTime creationDate) {

    public static PaymentResponse fromEntity(PaymentMethod payment) {
        return new PaymentResponse(payment.getIdPaymentMethod(), payment.getOrder().getIdOrder(), payment.getType(),
                payment.getTransactionStatus(), payment.getExternalReference(), payment.getCreationDate());
    }
}
