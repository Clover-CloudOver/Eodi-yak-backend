package com.eodi.yak.eodi_yak.domain.medicine.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record RestockRequest(
        @Schema(description = "약 이름", defaultValue = "타이레놀")
        String meName,

        @Schema(description = "약국 코드", defaultValue = "002")
        String paCode
) {}