package com.ewsaly.ewsaly.dto.qrcode;

import com.ewsaly.ewsaly.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQrCodeResponse {

    private Long id;

    private String publicToken;

    private ItemType itemType;

    private String itemName;

    private Boolean isActive;

    private QrCodeDesignDto design;
}
