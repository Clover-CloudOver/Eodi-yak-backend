package com.eodi.yak.eodi_yak.domain.pharmacy.repository;

import com.eodi.yak.eodi_yak.domain.pharmacy.entity.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PharmacyRepository extends JpaRepository<Pharmacy, String> {
    @Query(value = """
    SELECT p.* FROM pharmacy p
    WHERE (6371 * acos(cos(radians(?1)) * cos(radians(p.latitude)) 
    * cos(radians(p.longitude) - radians(?2))
    + sin(radians(?1)) * sin(radians(p.latitude))))
    <= ?3
    """, nativeQuery = true)
    Page<Pharmacy> findPharmaciesWithinRadius(
            double latitude,
            double longitude,
            int radius,
            Pageable pageable
    );
}
