package com.eodi.yak.eodi_yak.domain.pharmacy.service;

import com.eodi.yak.eodi_yak.domain.pharmacy.response.PageResponse;
import com.eodi.yak.eodi_yak.domain.pharmacy.response.PharmacyResponse;
import com.eodi.yak.eodi_yak.domain.pharmacy.entity.Pharmacy;
import com.eodi.yak.eodi_yak.domain.pharmacy.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;

    // 반경 내 약국 찾기
    public PageResponse<PharmacyResponse> findNearbyPharmacies(double latitude, double longitude, int radius, Pageable pageable) {
        // 반경 내 약국을 페이징 처리하여 가져옴
        Page<Pharmacy> pages = pharmacyRepository.findPharmaciesWithinRadius(latitude, longitude, radius, pageable);

        // Pharmacy를 PharmacyResponse로 변환
        List<PharmacyResponse> pharmacyResponses = pages.getContent().stream()
                .map(PharmacyResponse::from)
                .collect(Collectors.toList());

        return PageResponse.of(pages.getContent().stream().map(PharmacyResponse::from).toList());
    }

    // paCode를 기준으로 Pharmacy 조회
    public Pharmacy findById(String paCode) {
        return pharmacyRepository.findById(paCode)
                .orElseThrow(() -> new RuntimeException("Pharmacy not found with paCode: " + paCode));
    }

    // 두 지점 사이의 거리를 계산하는 Haversine 공식
    private boolean isWithinRadius(Pharmacy pharmacy, double latitude, double longitude, int radius) {
        double lat1 = Math.toRadians(latitude);
        double lon1 = Math.toRadians(longitude);
        double lat2 = Math.toRadians(pharmacy.getLatitude().doubleValue());
        double lon2 = Math.toRadians(pharmacy.getLongitude().doubleValue());

        // Haversine formula
        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dlon / 2) * Math.sin(dlon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 6371 * c; // 거리 (단위: km)

        return distance <= radius; // 반경 이내에 있으면 true
    }
}
