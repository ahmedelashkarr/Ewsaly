package com.ewsaly.ewsaly.dto.qrcode;

import com.ewsaly.ewsaly.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQrCodeRequest {

    @NotNull(message = "validation.qrcode.itemType.required")
    private ItemType itemType;

    @NotBlank(message = "validation.qrcode.itemName.required")
    @Size(max = 100, message = "validation.qrcode.itemName.size")
    private String itemName;

    private QrCodeDesignDto design;
}
