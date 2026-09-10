package com.ewsaly.ewsaly.dto.qrcode;

import com.ewsaly.ewsaly.enums.EyeStyle;
import com.ewsaly.ewsaly.enums.FrameStyle;
import com.ewsaly.ewsaly.enums.PatternStyle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeDesignDto {

    private String logoBrand;
    private String logoCustomUrl;
    private Boolean logoRound;
    private String patternColor;
    private String eyeColor;
    private String frameColor;
    private String backgroundColor;
    private PatternStyle patternStyle;
    private EyeStyle eyeStyle;
    private FrameStyle frameStyle;
}
