package com.takedrink.backend.service;

import com.takedrink.backend.dto.address.AddressResponse;
import com.takedrink.backend.dto.address.CreateAddressRequest;
import com.takedrink.backend.dto.address.UpdateAddressRequest;

import java.util.List;

public interface AddressService {

  AddressResponse createAddress(CreateAddressRequest request);

  List<AddressResponse> getMyAddresses();

  AddressResponse getAddressById(Long id);

  AddressResponse updateAddress(
          Long id,
          UpdateAddressRequest request
  );

  void deleteAddress(Long id);

  AddressResponse setDefaultAddress(Long id);
}
