package com.kingsman.FakeStoreAPIBack.DTO;

import com.kingsman.FakeStoreAPIBack.Enum.AddressType;

public record AddressRequest(String street, String city, String province, String postalCode, String country,
        AddressType type) {
}
