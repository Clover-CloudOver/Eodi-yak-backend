package com.eodi.yak.eodi_yak.domain.pharmacy.repository;

import com.eodi.yak.eodi_yak.domain.pharmacy.entity.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PharmacyRepository extends JpaRepository<Pharmacy, String> {
    @Query(
            value = """
        SELECT p FROM Pharmacy p 
        WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) 
        * cos(radians(p.longitude) - radians(:longitude)) 
        + sin(radians(:latitude)) * sin(radians(p.latitude)))) <= :radius
        """,
            countQuery = """
        SELECT COUNT(p) FROM Pharmacy p 
        WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) 
        * cos(radians(p.longitude) - radians(:longitude)) 
        + sin(radians(:latitude)) * sin(radians(p.latitude)))) <= :radius
        """
    )
    Page<Pharmacy> findPharmaciesWithinRadius(
            double latitude,
            double longitude,
            int radius,
            Pageable pageable
    );
}