package com.ewsaly.ewsaly.dto.qrcode;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQrCodeRequest {

    @Size(max = 100, message = "validation.qrcode.itemName.size")
    private String itemName;

    private Boolean isActive;

    private QrCodeDesignDto design;
}
