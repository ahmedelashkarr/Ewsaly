package com.ewsaly.ewsaly.dto.scanEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScanEventRequest {

    private String ipAddress;

    private String userAgent;
}
