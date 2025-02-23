package com.eodi.yak.eodi_yak.domain.reservation.response;

import com.eodi.yak.eodi_yak.domain.reservation.entity.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "예약 응답")
public record ReservationResponse(
        @Schema(description = "예약 id", defaultValue = "0")
        Long id,

        @Schema(description = "약국 코드", defaultValue = "JDQ4MTg4MSM1MSMkMSMkMCMkMDMkNDgxMzUxIzIxIyQyIyQ5IyQwMCQ0NjE0ODEjODEjJDEjJDIjJDgz")
        String pharmacyCode,

        @Schema(description = "사용자 이메일", defaultValue = "test@test.com")
        String medicineName,

        @Schema(description = "예약일시", defaultValue = "2025-02-10T07:59:48.540")
        LocalDateTime reservation_at,

        @Schema(description = "예약 상황", defaultValue = "wait")
        String status,

        @Schema(description = "수령기한", defaultValue = "2025-02-13T07:59:48.540")
        LocalDateTime pickup_deadline
) {
    public static ReservationResponse from(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getReservationId())
                .pharmacyCode(reservation.getMedicine().getPharmacy().getPaCode())
                .medicineName(reservation.getMedicine().getId().getMeName())
                .reservation_at(reservation.getReservationAt())
                .status(reservation.getStatus())
                .pickup_deadline(reservation.getPickupDeadline())
                .build();
    }
}
