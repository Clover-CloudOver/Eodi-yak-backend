package com.eodi.yak.eodi_yak.domain.reservation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "약 예약 요청")
public record ReservationRequest(
        @Schema(description = "사용자 고유 id(자동부여)", defaultValue = "1")
        String memberId,

        @Schema(description = "약국 코드", defaultValue = "JDQ4MTg4MSM1MSMkMSMkMCMkMDMkNDgxMzUxIzIxIyQyIyQ5IyQwMCQ0NjE0ODEjODEjJDEjJDIjJDgz")
        String pharmacyCode,

        @Schema(description = "약 이름", defaultValue = "타이레놀")
        String medicineName,

        @Schema(description = "예약 수향", defaultValue = "1")
        Integer quantity
) {
}
