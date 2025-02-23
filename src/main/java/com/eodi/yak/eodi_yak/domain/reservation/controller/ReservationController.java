package com.eodi.yak.eodi_yak.domain.reservation.controller;

import com.eodi.yak.eodi_yak.domain.auth.JWTUtil;
import com.eodi.yak.eodi_yak.domain.reservation.request.ReservationRequest;
import com.eodi.yak.eodi_yak.domain.reservation.response.ReservationResponse;
import com.eodi.yak.eodi_yak.domain.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservation")
@Tag(name = "Reservation", description = "어디약?! 약 예약 API")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final JWTUtil jwtUtil;

    @GetMapping("/list")
    @Operation(summary = "사용자 예약 목록", description = "특정 사용자의 예약 목록을 반환합니다.")
    public ResponseEntity<List<ReservationResponse>> getMemberReservations(
            @RequestHeader("X-USER-ID") String memberId) {

        // TODO: long type check 추가
        List<ReservationResponse> reservations = reservationService.getReservationsByUser(Long.valueOf(memberId));
        return ResponseEntity.ok(reservations);
    }

    // TODO: 약 예약 기능 추가
    @PostMapping
    @Operation(summary = "약 예약", description = "사용자가 특정 약을 예약합니다.")
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestHeader("X-USER-ID") String memberId,
            @RequestBody ReservationRequest request) {

        return ResponseEntity.ok(reservationService.createReservation(memberId, request));
    }
}
