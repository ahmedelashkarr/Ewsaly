package com.ewsaly.ewsaly.dto.emergency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmergencyContactRequest {

    @Size(max = 120, message = "validation.fullName.size")
    private String name;

    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "validation.phoneNumber.e164")
    private String phoneNumber;

    @Size(max = 60, message = "validation.relationship.size")
    private String relationship;
}
