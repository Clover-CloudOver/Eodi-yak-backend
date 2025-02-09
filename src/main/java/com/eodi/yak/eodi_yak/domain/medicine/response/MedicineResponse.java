package com.eodi.yak.eodi_yak.domain.medicine.response;

import com.eodi.yak.eodi_yak.domain.medicine.entity.Medicine;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MedicineResponse(
        String meName,
        String paCode,
        Integer meCount,
        LocalDateTime updatedAt
) {
    public static MedicineResponse from(Medicine medicine) {
        return MedicineResponse.builder()
                .meName(medicine.getId().getMeName())
                .paCode(medicine.getId().getPaCode())
                .meCount(medicine.getStock())
                .updatedAt(medicine.getUpdatedAt())
                .build();
    }
}
