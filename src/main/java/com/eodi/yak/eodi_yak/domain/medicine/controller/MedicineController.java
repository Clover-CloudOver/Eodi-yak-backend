package com.eodi.yak.eodi_yak.domain.medicine.controller;

import com.eodi.yak.eodi_yak.domain.auth.JWTUtil;
import com.eodi.yak.eodi_yak.domain.medicine.entity.Medicine;
import com.eodi.yak.eodi_yak.domain.medicine.request.RestockRequest;
import com.eodi.yak.eodi_yak.domain.medicine.response.MedicineResponse;
import com.eodi.yak.eodi_yak.domain.medicine.service.MedicineService;
import com.eodi.yak.eodi_yak.domain.pharmacy.entity.Pharmacy;
import com.eodi.yak.eodi_yak.domain.pharmacy.response.PageResponse;
import com.eodi.yak.eodi_yak.domain.pharmacy.response.PharmacyResponse;
import com.eodi.yak.eodi_yak.domain.pharmacy.service.PharmacyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/medicine")
@Tag(name = "Medicine", description = "어디약?! 약 관련 API")
@RequiredArgsConstructor
public class MedicineController {
    private final MedicineService medicineService;
    private final PharmacyService pharmacyService;
    private final JWTUtil jwtUtil;

    @GetMapping("/search")
    @Operation(summary = "약 검색", description = "입력한 약 이름을 포함하는 약국 리스트와 재고를 반환합니다.")
    public List<MedicineResponse> searchMedicine(@RequestParam(defaultValue="타이레놀") String name,
                                                 @RequestParam(defaultValue="37.5665") double latitude,
                                                 @RequestParam(defaultValue="126.9788") double longitude,
                                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "size", defaultValue = "20") int size) {
        // 반경 내 약국 찾기
        // TODO: 하드코딩된 반경 수정
        Integer radius = 100;
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

        // TODO: 검색어가 약 이름인지 검증
        // 약 정보 처리 및 재고가 0인 약을 추가
        List<MedicineResponse> response = new ArrayList<>();

        for (PharmacyResponse pharmacy : nearbyPharmacies.contents()) {
            boolean isMedicineFound = false;

            // 해당 약국에서 해당 약이 재고 있는지 확인
            for (Medicine medicine : medicines) {
                if (medicine.getPharmacy().getPaCode().equals(pharmacy.paCode()) && medicine.getId().getMeName().equals(name)) {
                    response.add(MedicineResponse.from(medicine));
                    isMedicineFound = true;
                }
            }

            // 해당 약국에 재고가 없는 경우, 재고 0으로 추가
            if (!isMedicineFound) {
                // 재고 0으로 Medicine 객체 생성 후 추가
                Pharmacy pharmacyEntity = pharmacyService.findById(pharmacy.paCode()); // 약국 정보 가져오기
                Medicine zeroStockMedicine = new Medicine(name, pharmacyEntity, 0);
                response.add(MedicineResponse.from(zeroStockMedicine)); // 재고 0인 약 추가
            }
        }

        return response;
    }

    // TODO: 약 재입고 신청 (의사 알림 1회 (최초), 재입고 알림 1회 (사용자))
    @PostMapping("/restocking-request")
    @Operation(summary = "재입고 신청",
            description = "입력한 약에 대해 재입고를 신청합니다.",
            security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<Object> createMember(
            @RequestHeader("X-USER-ID") String memberId,
            @RequestBody RestockRequest request) {

        Boolean isOk = medicineService.restockRequest(memberId, request);
        if (isOk) {
            return ResponseEntity.ok().build();
        } else {
            // 재입고 신청 실패 시 500 Internal Server Error 반환
            return ResponseEntity.internalServerError().build();
        }
    }

}
