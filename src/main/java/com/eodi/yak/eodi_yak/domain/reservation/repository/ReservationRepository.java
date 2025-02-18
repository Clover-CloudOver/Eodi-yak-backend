package com.eodi.yak.eodi_yak.domain.reservation.repository;

import com.eodi.yak.eodi_yak.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByMember_MemberId(Long memberId);
}
