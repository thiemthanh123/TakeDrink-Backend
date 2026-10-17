package com.takedrink.backend.service.impl;

import com.takedrink.backend.dto.address.AddressResponse;
import com.takedrink.backend.dto.address.CreateAddressRequest;
import com.takedrink.backend.dto.address.UpdateAddressRequest;
import com.takedrink.backend.entity.Address;
import com.takedrink.backend.entity.User;
import com.takedrink.backend.exception.AddressNotFoundException;
import com.takedrink.backend.exception.BadRequestException;
import com.takedrink.backend.exception.UnauthorizedException;
import com.takedrink.backend.repository.AddressRepository;
import com.takedrink.backend.repository.UserRepository;
import com.takedrink.backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

  private final AddressRepository addressRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public AddressResponse createAddress(CreateAddressRequest request) {
    if (request == null) {
      throw new BadRequestException("Address request must not be null");
    }

    User currentUser = getAuthenticatedUser();

    Address address = new Address();
    address.setUser(currentUser);

    applyAddressData(
            address,
            request.recipientName(),
            request.phone(),
            request.addressLine(),
            request.ward(),
            request.district(),
            request.province()
    );

    List<Address> existingAddresses =
            addressRepository.findByUserId(currentUser.getId());

    boolean shouldBeDefault =
            Boolean.TRUE.equals(request.defaultAddress())
                    || existingAddresses.stream()
                    .noneMatch(a -> Boolean.TRUE.equals(a.getDefaultAddress()));

    if (shouldBeDefault) {
      existingAddresses.forEach(a -> a.setDefaultAddress(false));
    }

    address.setDefaultAddress(shouldBeDefault);

    Address savedAddress = addressRepository.save(address);
    return toResponse(savedAddress);
  }

  @Override
  public List<AddressResponse> getMyAddresses() {
    User currentUser = getAuthenticatedUser();

    return addressRepository.findByUserId(currentUser.getId())
            .stream()
            .map(this::toResponse)
            .toList();
  }

  @Override
  public AddressResponse getAddressById(Long id) {
    Address address = getOwnedAddress(id);
    return toResponse(address);
  }

  @Override
  @Transactional
  public AddressResponse updateAddress(
          Long id,
          UpdateAddressRequest request
  ) {
    if (request == null) {
      throw new BadRequestException("Address request must not be null");
    }

    Address address = getOwnedAddress(id);

    applyAddressData(
            address,
            request.recipientName(),
            request.phone(),
            request.addressLine(),
            request.ward(),
            request.district(),
            request.province()
    );

    Address savedAddress = addressRepository.save(address);
    return toResponse(savedAddress);
  }

  @Override
  @Transactional
  public void deleteAddress(Long id) {
    Address address = getOwnedAddress(id);
    User currentUser = getAuthenticatedUser();

    boolean wasDefault = Boolean.TRUE.equals(address.getDefaultAddress());

    addressRepository.delete(address);

    if (wasDefault) {
      List<Address> remainingAddresses =
              addressRepository.findByUserId(currentUser.getId());

      if (!remainingAddresses.isEmpty()) {
        Address nextDefault = remainingAddresses.get(0);
        nextDefault.setDefaultAddress(true);
        addressRepository.save(nextDefault);
      }
    }
  }

  @Override
  @Transactional
  public AddressResponse setDefaultAddress(Long id) {
    Address selectedAddress = getOwnedAddress(id);
    User currentUser = getAuthenticatedUser();

    List<Address> addresses =
            addressRepository.findByUserId(currentUser.getId());

    for (Address address : addresses) {
      address.setDefaultAddress(
              address.getId().equals(selectedAddress.getId())
      );
    }

    addressRepository.saveAll(addresses);

    return toResponse(selectedAddress);
  }

  private Address getOwnedAddress(Long id) {
    if (id == null || id <= 0) {
      throw new BadRequestException(
              "Address ID must be a positive number"
      );
    }

    User currentUser = getAuthenticatedUser();

    return addressRepository
            .findByIdAndUserId(id, currentUser.getId())
            .orElseThrow(() -> new AddressNotFoundException(id));
  }

  private User getAuthenticatedUser() {
    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null
            || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())
            || authentication.getName() == null
            || authentication.getName().isBlank()) {
      throw new UnauthorizedException("Authentication required");
    }

    return userRepository
            .findByEmailIgnoreCase(authentication.getName().trim())
            .orElseThrow(() -> new UnauthorizedException(
                    "Authenticated user was not found"
            ));
  }

  private void applyAddressData(
          Address address,
          String recipientName,
          String phone,
          String addressLine,
          String ward,
          String district,
          String province
  ) {
    if (recipientName == null || recipientName.isBlank()) {
      throw new BadRequestException("Recipient name is required");
    }

    if (phone == null || phone.isBlank()) {
      throw new BadRequestException("Phone number is required");
    }

    if (addressLine == null || addressLine.isBlank()) {
      throw new BadRequestException("Address line is required");
    }

    address.setRecipientName(recipientName.trim());
    address.setPhone(phone.trim());
    address.setAddressLine(addressLine.trim());
    address.setWard(normalizeOptionalText(ward));
    address.setDistrict(normalizeOptionalText(district));
    address.setProvince(normalizeOptionalText(province));
  }

  private String normalizeOptionalText(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    return value.trim();
  }

  private AddressResponse toResponse(Address address) {
    return new AddressResponse(
            address.getId(),
            address.getRecipientName(),
            address.getPhone(),
            address.getAddressLine(),
            address.getWard(),
            address.getDistrict(),
            address.getProvince(),
            address.getDefaultAddress(),
            address.getCreatedAt(),
            address.getUpdatedAt()
    );
  }
}
