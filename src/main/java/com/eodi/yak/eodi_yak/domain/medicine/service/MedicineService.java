package com.eodi.yak.eodi_yak.domain.medicine.service;

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
}
