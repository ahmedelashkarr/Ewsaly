package com.ewsaly.ewsaly.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "validation.fullName.required")
    @Size(max = 120, message = "validation.fullName.size")
    private String fullName;

    @NotBlank(message = "validation.phoneNumber.required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "validation.phoneNumber.e164")
    private String phoneNumber;

    @NotBlank(message = "validation.password.required")
    private String password;
}
