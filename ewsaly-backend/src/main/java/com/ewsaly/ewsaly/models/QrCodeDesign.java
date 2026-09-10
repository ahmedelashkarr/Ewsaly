package com.ewsaly.ewsaly.models;

import com.ewsaly.ewsaly.enums.EyeStyle;
import com.ewsaly.ewsaly.enums.FrameStyle;
import com.ewsaly.ewsaly.enums.PatternStyle;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrCodeDesign {

    // e.g. "Bmw", "Toyota"
    @Column(name = "logo_brand", length = 50)
    private String logoBrand;

    @Column(name = "logo_custom_url", length = 512)
    private String logoCustomUrl;

    @Column(name = "logo_round", nullable = false)
    @Builder.Default
    private Boolean logoRound = true;

    @Column(name = "pattern_color", nullable = false, length = 7)
    @Builder.Default
    private String patternColor = "#000000";

    @Column(name = "eye_color", nullable = false, length = 7)
    @Builder.Default
    private String eyeColor = "#000000";

    @Column(name = "frame_color", nullable = false, length = 7)
    @Builder.Default
    private String frameColor = "#000000";

    @Column(name = "background_color", length = 7)
    @Builder.Default
    private String backgroundColor = "#FFFFFF";

    @Enumerated(EnumType.STRING)
    @Column(name = "pattern_style", nullable = false)
    @Builder.Default
    private PatternStyle patternStyle = PatternStyle.SQUARE;

    @Enumerated(EnumType.STRING)
    @Column(name = "eye_style", nullable = false)
    @Builder.Default
    private EyeStyle eyeStyle = EyeStyle.SQUARE;

    @Enumerated(EnumType.STRING)
    @Column(name = "frame_style", nullable = false)
    @Builder.Default
    private FrameStyle frameStyle = FrameStyle.SQUARE;
}
