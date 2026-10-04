package com.kingsman.FakeStoreAPIBack.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kingsman.FakeStoreAPIBack.DTO.AddressRequest;
import com.kingsman.FakeStoreAPIBack.DTO.AddressResponse;
import com.kingsman.FakeStoreAPIBack.exception.ConflictException;
import com.kingsman.FakeStoreAPIBack.exception.NotFoundException;
import com.kingsman.FakeStoreAPIBack.models.Address;
import com.kingsman.FakeStoreAPIBack.models.User;
import com.kingsman.FakeStoreAPIBack.repositories.AddressRepository;
import com.kingsman.FakeStoreAPIBack.repositories.OrderRepository;
import com.kingsman.FakeStoreAPIBack.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CurrentUserService currentUserService;

    // Devuelve todas las direcciones de un usuario. 404 si el usuario no existe,
    // para distinguirlo de un usuario que existe pero todavía no cargó direcciones (lista vacía).
    // En todos los métodos, un CLIENT solo puede operar sobre sus propias direcciones.
    @Transactional(readOnly = true)
    public List<AddressResponse> getByUser(Long userId) {
        currentUserService.checkOwnerOrAdmin(userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("No existe el usuario con id " + userId);
        }
        return addressRepository.findByUserId(userId).stream().map(AddressResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public AddressResponse getById(Long id) {
        return AddressResponse.fromEntity(findAddress(id));
    }

    @Transactional
    public AddressResponse create(Long userId, AddressRequest request) {
        currentUserService.checkOwnerOrAdmin(userId);
        validate(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("No existe el usuario con id " + userId));

        Address address = new Address();
        address.setUser(user);
        copyFields(request, address);
        return AddressResponse.fromEntity(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse update(Long id, AddressRequest request) {
        validate(request);
        Address address = findAddress(id);
        copyFields(request, address);
        return AddressResponse.fromEntity(address);
    }

    // Una dirección usada como envío en alguna compra no se borra: la compra la necesita como historial
    @Transactional
    public void delete(Long id) {
        findAddress(id);
        if (orderRepository.countByShippingAddress(id) > 0) {
            throw new ConflictException("La dirección está usada en compras y no se puede borrar");
        }
        addressRepository.deleteById(id);
    }

    // Busca la dirección y verifica que sea del usuario logueado (o que sea ADMIN)
    private Address findAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe la dirección con id " + id));
        currentUserService.checkOwnerOrAdmin(address.getUser().getIdUser());
        return address;
    }

    private void validate(AddressRequest request) {
        RequestValidator.requireText(request.street(), "street");
        RequestValidator.requireText(request.city(), "city");
        RequestValidator.requireText(request.province(), "province");
        RequestValidator.requireText(request.postalCode(), "postalCode");
        RequestValidator.requireText(request.country(), "country");
        RequestValidator.requireNotNull(request.type(), "type");
    }

    private void copyFields(AddressRequest request, Address address) {
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setProvince(request.province());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());
        address.setType(request.type());
    }
}
