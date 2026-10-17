
package com.takedrink.backend.controller;

import com.takedrink.backend.dto.ApiResponse;
import com.takedrink.backend.dto.address.AddressResponse;
import com.takedrink.backend.dto.address.CreateAddressRequest;
import com.takedrink.backend.dto.address.UpdateAddressRequest;
import com.takedrink.backend.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

  private final AddressService addressService;

  /**
   * Create a new address for the authenticated user.
   */
  @PostMapping
  public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
          @Valid @RequestBody CreateAddressRequest request
  ) {
    AddressResponse addressResponse =
            addressService.createAddress(request);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                    "Address created successfully",
                    addressResponse
            ));
  }

  /**
   * Get all addresses belonging to the authenticated user.
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses() {
    List<AddressResponse> addresses =
            addressService.getMyAddresses();

    return ResponseEntity
            .ok(ApiResponse.success(
                    "Addresses retrieved successfully",
                    addresses
            ));
  }

  /**
   * Get an address by ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
          @PathVariable Long id
  ) {
    AddressResponse addressResponse =
            addressService.getAddressById(id);

    return ResponseEntity
            .ok(ApiResponse.success(
                    "Address retrieved successfully",
                    addressResponse
            ));
  }

  /**
   * Update an existing address.
   */
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
          @PathVariable Long id,
          @Valid @RequestBody UpdateAddressRequest request
  ) {
    AddressResponse addressResponse =
            addressService.updateAddress(id, request);

    return ResponseEntity
            .ok(ApiResponse.success(
                    "Address updated successfully",
                    addressResponse
            ));
  }

  /**
   * Delete an address.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteAddress(
          @PathVariable Long id
  ) {
    addressService.deleteAddress(id);

    return ResponseEntity
            .ok(ApiResponse.success(
                    "Address deleted successfully",
                    null
            ));
  }

  /**
   * Set an address as the default address.
   */
  @PatchMapping("/{id}/default")
  public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
          @PathVariable Long id
  ) {
    AddressResponse addressResponse =
            addressService.setDefaultAddress(id);

    return ResponseEntity
            .ok(ApiResponse.success(
                    "Default address updated successfully",
                    addressResponse
            ));
  }
}
