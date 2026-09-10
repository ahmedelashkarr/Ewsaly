package com.ewsaly.ewsaly.dto.emergency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactResponse {
    private Long id;
    private String name;
    private String phoneNumber;
    private String relationship;
}
