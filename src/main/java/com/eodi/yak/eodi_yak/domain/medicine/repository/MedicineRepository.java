package com.eodi.yak.eodi_yak.domain.medicine.repository;

import com.eodi.yak.eodi_yak.domain.medicine.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, String> {
    List<Medicine> findById_MeNameContaining(String meName);
    List<Medicine> findById_MeNameContainingAndPharmacy_PaCodeIn(String meName, List<String> pharmacyCodes);
}
