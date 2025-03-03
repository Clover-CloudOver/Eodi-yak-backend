package com.eodi.yak.eodi_yak.domain.medicine.service;

import com.eodi.yak.eodi_yak.domain.medicine.request.RestockRequest;
import com.eodi.yak.eodi_yak.domain.medicine.response.MedicineResponse;
import com.eodi.yak.eodi_yak.domain.medicine.entity.Medicine;
import com.eodi.yak.eodi_yak.domain.medicine.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public List<MedicineResponse> findMedicine(String name) {
        List<Medicine> medicines = medicineRepository.findById_MeNameContaining(name);
        return medicines.stream().map(MedicineResponse::from).collect(Collectors.toList());
    }

    public List<Medicine> findMedicinesByNameAndPharmacyCodes(String name, List<String> pharmacyCodes) {
        return medicineRepository.findById_MeNameContainingAndPharmacy_PaCodeIn(name, pharmacyCodes);
    }

    // TODO: 약 재입고 신청 (의사 알림 1회 (최초), 재입고 알림 1회 (사용자))
    public boolean restockRequest(String memberId, RestockRequest request){
        try {

            // 요청된 약에 대해 재입고 신청을 처리
            //Medicine medicine = medicineRepository.findById_MeNameAndId_PaCode(request.meName(), request.paCode())
                    //.orElseThrow(() -> new IllegalArgumentException("해당 약을 찾을 수 없습니다."));

            // TODO: 의사에게 최초 1회 알림 전송 (재입고 요청 알림)

            // TODO: 재고 +1 시, 사용자에게 재입고 알림 1회 전송
            return true; // 성공적으로 처리됨
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 처리 실패
        }
    }

}
