package com.ewsaly.ewsaly.dto.scanEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanEventQueryRequest {

    private LocalDateTime after;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;
}
