package com.kingsman.FakeStoreAPIBack.DTO;

import com.kingsman.FakeStoreAPIBack.Enum.AddressType;
import com.kingsman.FakeStoreAPIBack.models.Address;

public record AddressResponse(Long idAddress, Long idUser, String street, String city, String province,
        String postalCode, String country, AddressType type) {

    public static AddressResponse fromEntity(Address address) {
        return new AddressResponse(address.getIdAddress(), address.getUser().getIdUser(), address.getStreet(),
                address.getCity(), address.getProvince(), address.getPostalCode(), address.getCountry(),
                address.getType());
    }
}
