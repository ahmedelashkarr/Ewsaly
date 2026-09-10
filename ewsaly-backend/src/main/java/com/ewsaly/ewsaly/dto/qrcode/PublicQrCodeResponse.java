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
public class PublicQrCodeResponse {

    private String publicToken;

    private ItemType itemType;

    private String contactName;
}
