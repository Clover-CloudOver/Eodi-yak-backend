package com.eodi.yak.eodi_yak.domain.medicine.response;

import com.eodi.yak.eodi_yak.domain.medicine.entity.Medicine;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record MedicineResponse(
        String meName,
        String paCode,
        Integer meCount,
        BigDecimal paLatitude, // 약국 위도
        BigDecimal paLongitude, // 약국 경도
        LocalDateTime updatedAt
) {
    public static MedicineResponse from(Medicine medicine) {
        return MedicineResponse.builder()
                .meName(medicine.getId().getMeName())
                .paCode(medicine.getId().getPaCode())
                .meCount(medicine.getStock())
                .paLatitude(medicine.getPharmacy().getLatitude())
                .paLongitude(medicine.getPharmacy().getLongitude())
                .updatedAt(medicine.getUpdatedAt())
                .build();
    }
}
