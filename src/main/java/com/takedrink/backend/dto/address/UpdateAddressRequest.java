package com.takedrink.backend.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAddressRequest(

        @NotBlank(message = "Recipient name is required")
        @Size(max = 150, message = "Recipient name must not exceed 150 characters")
        String recipientName,

        @NotBlank(message = "Phone number is required")
        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        String phone,

        @NotBlank(message = "Address line is required")
        @Size(max = 255, message = "Address line must not exceed 255 characters")
        String addressLine,

        @Size(max = 100, message = "Ward must not exceed 100 characters")
        String ward,

        @Size(max = 100, message = "District must not exceed 100 characters")
        String district,

        @Size(max = 100, message = "Province must not exceed 100 characters")
        String province
) {}
