package com.eodi.yak.eodi_yak.domain.pharmacy.response;

import com.eodi.yak.eodi_yak.domain.pharmacy.entity.Pharmacy;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalTime;

@Builder
public record PharmacyResponse(
        String paCode,
        String paName,
        BigDecimal paLatitude,
        BigDecimal paLongitude,
        String paPhoneNumber,
        String paEmail,
        String address
) {
    public static PharmacyResponse from(Pharmacy pharmacy) {
        return PharmacyResponse.builder()
                .paCode(pharmacy.getPaCode())
                .paName(pharmacy.getPaName())
                .paLatitude(pharmacy.getLatitude())
                .paLongitude(pharmacy.getLongitude())
                .paPhoneNumber(pharmacy.getPhoneNumber())
                .paEmail(pharmacy.getEmail())
                .address(pharmacy.getAddress())
                .build();
    }
}
