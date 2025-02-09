package com.eodi.yak.eodi_yak.domain.pharmacy.controller;

import com.eodi.yak.eodi_yak.domain.pharmacy.response.PageResponse;
import com.eodi.yak.eodi_yak.domain.pharmacy.response.PharmacyResponse;
import com.eodi.yak.eodi_yak.domain.pharmacy.service.PharmacyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacy")
@Tag(name = "Pharmacy", description = "어디약?! 약국 관련 API")
@RequiredArgsConstructor
public class PharmacyController {

    private final PharmacyService pharmacyService;

    @GetMapping("/nearby")
    @Operation(summary = "근처 약국 조회", description = "사용자의 위치를 기준으로 반경 내 약국 리스트를 조회합니다.")
    @Parameter(name = "page", description = "페이지 번호")
    @Parameter(name = "size", description = "페이지 크기")
    public ResponseEntity<PageResponse<PharmacyResponse>> getNearbyPharmacies(
            @RequestParam(defaultValue="37.5665") double latitude,
            @RequestParam(defaultValue="126.9788") double longitude,
            @RequestParam(defaultValue = "100") int radius,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<PharmacyResponse> response = pharmacyService.findNearbyPharmacies(latitude, longitude, radius, pageable);
        return ResponseEntity.ok(response);
    }
}
