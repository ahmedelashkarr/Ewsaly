package com.ewsaly.ewsaly.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResendOtpRequest {

    @NotBlank(message = "validation.phoneNumber.required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "validation.phoneNumber.e164")
    private String phoneNumber;
}
