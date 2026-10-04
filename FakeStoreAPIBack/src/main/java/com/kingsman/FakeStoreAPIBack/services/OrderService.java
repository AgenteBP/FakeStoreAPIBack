package com.kingsman.FakeStoreAPIBack.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kingsman.FakeStoreAPIBack.DTO.OrderItemRequest;
import com.kingsman.FakeStoreAPIBack.DTO.OrderItemResponse;
import com.kingsman.FakeStoreAPIBack.DTO.OrderItemView;
import com.kingsman.FakeStoreAPIBack.DTO.OrderRequest;
import com.kingsman.FakeStoreAPIBack.DTO.OrderResponse;
import com.kingsman.FakeStoreAPIBack.DTO.OrderStatusRequest;
import com.kingsman.FakeStoreAPIBack.Enum.OrderStatus;
import com.kingsman.FakeStoreAPIBack.exception.BadRequestException;
import com.kingsman.FakeStoreAPIBack.exception.ConflictException;
import com.kingsman.FakeStoreAPIBack.exception.NotFoundException;
import com.kingsman.FakeStoreAPIBack.models.Address;
import com.kingsman.FakeStoreAPIBack.models.Order;
import com.kingsman.FakeStoreAPIBack.models.OrderItem;
import com.kingsman.FakeStoreAPIBack.models.PaymentMethod;
import com.kingsman.FakeStoreAPIBack.models.Product;
import com.kingsman.FakeStoreAPIBack.models.User;
import com.kingsman.FakeStoreAPIBack.repositories.AddressRepository;
import com.kingsman.FakeStoreAPIBack.repositories.InventoryRepository;
import com.kingsman.FakeStoreAPIBack.repositories.OrderItemRepository;
import com.kingsman.FakeStoreAPIBack.repositories.OrderRepository;
import com.kingsman.FakeStoreAPIBack.repositories.PaymentMethodRepository;
import com.kingsman.FakeStoreAPIBack.repositories.ProductRepository;
import com.kingsman.FakeStoreAPIBack.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    // Datos con los que se registra el pago simulado en payment_methods
    private static final String SIMULATED_PAYMENT_TYPE = "SIMULATED";
    private static final String APPROVED_TRANSACTION = "APPROVED";

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CurrentUserService currentUserService;

    @Value("${orders.payment-delay-minutes}")
    private long paymentDelayMinutes;

    @Value("${orders.shipping-delay-minutes}")
    private long shippingDelayMinutes;

    @Value("${orders.delivery-delay-minutes}")
    private long deliveryDelayMinutes;

    // Todas las compras, de la más nueva a la más vieja. Solo ADMIN (lo restringe SecurityConfig).
    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        return orderRepository.findAllNewestFirst().stream().map(this::toResponse).toList();
    }

    // Un CLIENT solo puede ver sus propias compras
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe la compra con id " + id));
        currentUserService.checkOwnerOrAdmin(order.getUser().getIdUser());
        return toResponse(order);
    }

    // Compras de un usuario, de la más nueva a la más vieja. 404 si el usuario no existe.
    @Transactional(readOnly = true)
    public List<OrderResponse> getByUser(Long userId) {
        currentUserService.checkOwnerOrAdmin(userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("No existe el usuario con id " + userId);
        }
        return orderRepository.findByUserNewestFirst(userId).stream().map(this::toResponse).toList();
    }

    // Crea una compra en estado PENDING para el usuario logueado.
    // Pasos:
    //   1. Toma el usuario del token y valida que la dirección de envío sea suya.
    //   2. Por cada producto pedido: reserva el stock y guarda el ítem con el precio actual
    //      del producto como unit_price (queda fijo aunque después cambie el precio).
    //   3. Calcula el total sumando cantidad x precio de cada ítem.
    // Todo corre en una sola transacción: si un producto no tiene stock se responde 409
    // y se deshacen también las reservas de los productos anteriores.
    @Transactional
    public OrderResponse create(OrderRequest request) {
        RequestValidator.requireNotNull(request.idShippingAddress(), "idShippingAddress");
        if (request.items() == null || request.items().isEmpty()) {
            throw new BadRequestException("La compra debe tener al menos un producto");
        }

        Long currentUserId = currentUserService.get().getIdUser();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("No existe el usuario con id " + currentUserId));
        Address address = addressRepository.findById(request.idShippingAddress())
                .orElseThrow(() -> new NotFoundException(
                        "No existe la dirección con id " + request.idShippingAddress()));
        if (!address.getUser().getIdUser().equals(user.getIdUser())) {
            throw new BadRequestException("La dirección de envío no pertenece al usuario");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(address);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setTotal(BigDecimal.ZERO);
        orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.items()) {
            OrderItem item = createItem(order, itemRequest);
            total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setTotal(total);

        return toResponse(order);
    }

    // El dueño de la compra (o un ADMIN) la paga. Es un pago simulado: siempre se aprueba.
    // Registra el pago en payment_methods y pasa la compra de PENDING a PAID.
    @Transactional
    public OrderResponse pay(Long id) {
        Order order = findOrderForUpdate(id);
        currentUserService.checkOwnerOrAdmin(order.getUser().getIdUser());
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ConflictException("Solo se puede pagar una compra pendiente");
        }

        markAsPaid(order);
        return toResponse(order);
    }

    // El dueño de la compra (o un ADMIN) la cancela. Solo mientras está PENDING;
    // el stock reservado vuelve a estar disponible.
    @Transactional
    public OrderResponse cancel(Long id) {
        Order order = findOrderForUpdate(id);
        currentUserService.checkOwnerOrAdmin(order.getUser().getIdUser());
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ConflictException("Solo se puede cancelar una compra pendiente");
        }

        changeTo(order, OrderStatus.CANCELLED);
        return toResponse(order);
    }

    // Cambio de estado manual del ADMIN (lo restringe SecurityConfig).
    // Permite cualquier paso del flujo, incluso cancelar una compra ya pagada.
    // Si la pasa a PAID se registra el pago simulado, igual que cuando paga el cliente.
    @Transactional
    public OrderResponse changeStatus(Long id, OrderStatusRequest request) {
        RequestValidator.requireNotNull(request.status(), "status");
        Order order = findOrderForUpdate(id);
        if (request.status() == OrderStatus.PAID) {
            markAsPaid(order);
        } else {
            changeTo(order, request.status());
        }
        return toResponse(order);
    }

    // Baja de un usuario: cancela sus compras que todavía no salieron (PENDING y PAID) y devuelve el stock.
    // Las SHIPPED y DELIVERED quedan como historial. Devuelve cuántas compras canceló.
    // Corre dentro de la transacción de UserService.deactivate: si algo falla, no se da de baja.
    @Transactional
    public int cancelNotShippedOrdersOfUser(Long userId) {
        int cancelled = 0;
        for (Long orderId : orderRepository.findNotShippedIdsByUser(userId)) {
            Order order = findOrderForUpdate(orderId);
            // Puede haber avanzado a SHIPPED entre la consulta y el bloqueo: en ese caso se deja
            boolean canCancel = order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PAID;
            if (canCancel) {
                changeTo(order, OrderStatus.CANCELLED);
                cancelled++;
            }
        }
        return cancelled;
    }

    // ---- Simulación automática (la llama OrderStatusScheduler) ----

    // Ids de las compras que llevan más de paymentDelayMinutes en PENDING
    @Transactional(readOnly = true)
    public List<Long> findOrdersToAutoPay() {
        return orderRepository.findPendingIdsBefore(LocalDateTime.now().minusMinutes(paymentDelayMinutes));
    }

    // Paga una compra automáticamente. Corre en su propia transacción, una por compra.
    // Si mientras tanto el cliente la pagó o la canceló, ya no está PENDING y no se hace nada.
    // Devuelve true si la pagó.
    @Transactional
    public boolean autoPay(Long id) {
        Order order = findOrderForUpdate(id);
        if (order.getStatus() != OrderStatus.PENDING) {
            return false;
        }
        markAsPaid(order);
        return true;
    }

    // Pasa a SHIPPED las compras que están PAID hace más de shippingDelayMinutes.
    // Como momento del pago se usa update_date: después de PAID la compra no cambia hasta el envío.
    @Transactional
    public int shipPaidOrders() {
        LocalDateTime now = LocalDateTime.now();
        return orderRepository.shipPaidOrdersBefore(now.minusMinutes(shippingDelayMinutes), now);
    }

    // Pasa a DELIVERED las compras que están SHIPPED hace más de deliveryDelayMinutes
    @Transactional
    public int deliverShippedOrders() {
        LocalDateTime now = LocalDateTime.now();
        return orderRepository.deliverShippedOrdersBefore(now.minusMinutes(deliveryDelayMinutes), now);
    }

    // ---- Métodos internos ----

    // Registra el pago simulado y pasa la compra a PAID
    private void markAsPaid(Order order) {
        PaymentMethod payment = new PaymentMethod();
        payment.setOrder(order);
        payment.setType(SIMULATED_PAYMENT_TYPE);
        payment.setTransactionStatus(APPROVED_TRANSACTION);
        payment.setExternalReference("SIM-" + order.getIdOrder());
        paymentMethodRepository.save(payment);

        changeTo(order, OrderStatus.PAID);
    }

    // Valida el cambio de estado, mueve el stock y guarda el nuevo estado.
    // Un cambio fuera del flujo PENDING -> PAID -> SHIPPED -> DELIVERED responde 409.
    private void changeTo(Order order, OrderStatus next) {
        OrderStatus current = order.getStatus();
        if (!isValidTransition(current, next)) {
            throw new ConflictException("No se puede pasar una compra de " + current + " a " + next);
        }
        updateStock(order.getIdOrder(), current, next);
        order.setStatus(next);
    }

    // Valida el producto pedido, reserva su stock y guarda la línea de la compra
    private OrderItem createItem(Order order, OrderItemRequest itemRequest) {
        RequestValidator.requireNotNull(itemRequest.idProduct(), "idProduct");
        if (itemRequest.quantity() == null || itemRequest.quantity() <= 0) {
            throw new BadRequestException("La cantidad de cada producto debe ser mayor a 0");
        }

        Product product = productRepository.findById(itemRequest.idProduct())
                .orElseThrow(() -> new NotFoundException("No existe el producto con id " + itemRequest.idProduct()));
        if (!product.getActive()) {
            throw new ConflictException("El producto '" + product.getName() + "' no está disponible");
        }

        // reserve() solo descuenta si el stock alcanza; si no modificó ninguna fila, no alcanzó
        int updatedRows = inventoryRepository.reserve(product.getIdProduct(), itemRequest.quantity());
        if (updatedRows == 0) {
            throw new ConflictException("Stock insuficiente para '" + product.getName() + "'");
        }

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(itemRequest.quantity());
        item.setUnitPrice(product.getPrice());
        return orderItemRepository.save(item);
    }

    // Define a qué estado se puede pasar desde cada estado
    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case PENDING -> next == OrderStatus.PAID || next == OrderStatus.CANCELLED;
            case PAID -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }

    // Mueve el stock de cada ítem según el nuevo estado:
    //   PENDING -> PAID:       lo reservado se descuenta definitivamente
    //   PENDING -> CANCELLED:  lo reservado vuelve a disponible
    //   PAID    -> CANCELLED:  ya se había descontado, se devuelve a disponible
    //   SHIPPED y DELIVERED no mueven stock.
    private void updateStock(Long orderId, OrderStatus current, OrderStatus next) {
        for (OrderItemView item : orderItemRepository.findItemsByOrder(orderId)) {
            switch (next) {
                case PAID -> inventoryRepository.confirmReserved(item.getIdProduct(), item.getQuantity());
                case CANCELLED -> {
                    if (current == OrderStatus.PENDING) {
                        inventoryRepository.releaseReserved(item.getIdProduct(), item.getQuantity());
                    } else {
                        inventoryRepository.returnToAvailable(item.getIdProduct(), item.getQuantity());
                    }
                }
                default -> {
                    // SHIPPED y DELIVERED no mueven stock
                }
            }
        }
    }

    // Busca la compra bloqueando su fila (ver OrderRepository.findByIdForUpdate)
    private Order findOrderForUpdate(Long id) {
        return orderRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("No existe la compra con id " + id));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = orderItemRepository.findItemsByOrder(order.getIdOrder()).stream()
                .map(OrderItemResponse::fromView)
                .toList();
        User customer = order.getUser();
        return new OrderResponse(order.getIdOrder(), customer.getIdUser(), customer.getUserName(),
                customer.getEmail(), order.getShippingAddress().getIdAddress(), order.getOrderDate(),
                order.getStatus(), order.getTotal(), items);
    }
}
