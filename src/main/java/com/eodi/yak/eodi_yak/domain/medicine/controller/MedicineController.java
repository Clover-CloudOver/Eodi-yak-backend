package com.eodi.yak.eodi_yak.domain.medicine.controller;

import com.eodi.yak.eodi_yak.domain.medicine.entity.Medicine;
import com.eodi.yak.eodi_yak.domain.medicine.request.RestockRequest;
import com.eodi.yak.eodi_yak.domain.medicine.response.MedicineResponse;
import com.eodi.yak.eodi_yak.domain.medicine.service.MedicineService;
import com.eodi.yak.eodi_yak.domain.pharmacy.response.PageResponse;
import com.eodi.yak.eodi_yak.domain.pharmacy.response.PharmacyResponse;
import com.eodi.yak.eodi_yak.domain.pharmacy.service.PharmacyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/medicine")
@Tag(name = "Medicine", description = "어디약?! 약 관련 API")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;
    private final PharmacyService pharmacyService;

    @GetMapping("/search")
    @Operation(summary = "약 검색", description = "입력한 약 이름을 포함하는 약국 리스트와 재고를 반환합니다.")
    public List<MedicineResponse> searchMedicine(@RequestParam(defaultValue="타이레놀") String name,
                                                 @RequestParam(defaultValue="37.5665") double latitude,
                                                 @RequestParam(defaultValue="126.9788") double longitude,
                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "size", defaultValue = "20") int size) {
        // 반경 내 약국 찾기
        Integer radius = 5;
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<PharmacyResponse> nearbyPharmacies = pharmacyService.findNearbyPharmacies(latitude, longitude, radius, pageable);


        System.out.println("약 이름: " + name);
        // 반경 내 약국들의 paCode 리스트 추출
        List<String> pharmacyCodes = new ArrayList<>();
        for (PharmacyResponse pharmacy : nearbyPharmacies.contents()) {
            System.out.println("반경 내 약국의 pacode: " + pharmacy.paCode());
            pharmacyCodes.add(pharmacy.paCode());
        }

        // 약 이름을 포함하는 약국 리스트와 재고 찾기 (반경 내 약국으로 필터링)
        List<Medicine> medicines = medicineService.findMedicinesByNameAndPharmacyCodes(name, pharmacyCodes);

        // 약 정보를 MedicineResponse로 변환하여 반환
        return medicines.stream()
                .map(MedicineResponse::from)
                .collect(Collectors.toList());
    }

    // TODO: 약 재입고 신청 (의사 알림 1회 (최초), 재입고 알림 1회 (사용자))
    @PostMapping("/restocking-request")
    @Operation(summary = "재입고 신청", description = "입력한 약에 대해 재입고를 신청합니다.")
    public ResponseEntity<Object> createMember(@RequestBody RestockRequest request) {
        Boolean isOk = medicineService.restockRequest(request);
        if (isOk) {
            return ResponseEntity.ok().build();
        } else {
            // 재입고 신청 실패 시 500 Internal Server Error 반환
            return ResponseEntity.internalServerError().build();
        }
    }

}
