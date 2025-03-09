package com.eodi.yak.eodi_yak.domain.reservation.service;

// member entity를 reservation entity 파일 안에 복사
import com.eodi.yak.eodi_yak.domain.reservation.GrpcMedicineClient;
import com.eodi.yak.eodi_yak.domain.reservation.GrpcMemberClient;
import com.eodi.yak.eodi_yak.domain.reservation.entity.*;
import com.eodi.yak.eodi_yak.domain.reservation.request.ReservationRequest;
import com.eodi.yak.eodi_yak.domain.reservation.response.ReservationResponse;
import com.eodi.yak.eodi_yak.domain.reservation.repository.ReservationRepository;
import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final GrpcMemberClient grpcMemberClient;
    private final GrpcMedicineClient grpcMedicineClient;

    // 특정 사용자 예약 목록 조회
    public List<ReservationResponse> getReservationsByUser(Long memberId) {
        return reservationRepository.findByMember_MemberId(memberId)
                .stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    // 예약 생성
    @Transactional
    public ReservationResponse createReservation(String memberId, ReservationRequest request) {
        // (1) gRPC 호출을 통해 사용자 정보 조회
        member.Member.MemberRequest memberGrpcRequest = member.Member.MemberRequest.newBuilder()
                .setMemberId(memberId)
                .build();

        member.Member.MemberResponse memberGrpcResponse;

        try {
            memberGrpcResponse = grpcMemberClient.getMemberId(memberGrpcRequest);
            // memberGrpcResponse를 정상적으로 처리
        } catch (io.grpc.StatusRuntimeException e) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
            // TODO: err 처리
            //throw new MemberNotFoundException("사용자를 찾을 수 없습니다.", e);
        }

        Member member = new Member();
        // TODO: 필수적으로 member entity는 필요하다 (many-to-one)
        member.setMemberId(Long.valueOf(memberGrpcResponse.getMemberId()));
        member.setMemberEmail(memberGrpcResponse.getEmail());
        member.setPhoneNumber(memberGrpcResponse.getPhoneNumber());


        // (2) gRPC 호출을 통해 약 정보 조회
        medicine.Medicine.MedicineRequest medicineGrpcRequest = medicine.Medicine.MedicineRequest.newBuilder()
                .setMedicineName(request.medicineName())
                .setPharmacyCode(request.pharmacyCode())
                .build();

        medicine.Medicine.MedicineResponse medicineGrpcResponse;

        try {
            medicineGrpcResponse = grpcMedicineClient.getMedicineById(medicineGrpcRequest);
        } catch (io.grpc.StatusRuntimeException e) {
            throw new IllegalArgumentException("약을 찾을 수 없습니다.");
            // TODO: err 처리
        }

        Medicine medicine = new Medicine();
        // TODO: 필수적으로 medicine entity는 필요하다 (many-to-one)

        medicine.setId(new MedicineId(medicineGrpcResponse.getMedicineName(), medicineGrpcResponse.getPharmacyCode()));
        medicine.setStock(medicineGrpcResponse.getStock());
        medicine.setPharmacy(new Pharmacy(medicineGrpcResponse.getPharmacyCode(), medicineGrpcResponse.getPharmacyName(), new BigDecimal(medicineGrpcResponse.getPharmacyLatitude()), new BigDecimal(medicineGrpcResponse.getPharmacyLongitude()), medicineGrpcResponse.getPharmacyPhoneNumber(), medicineGrpcResponse.getPharmacyEmail(), medicineGrpcResponse.getPharmacyAddress()));
        //medicine.setPharmacy(medicineGrpcResponse.getPharmacy()); //???
        // TODO: medicine에서 pharmacy 정보를 조회할 적절한 방법 고민
        Timestamp grpcTimestamp = medicineGrpcResponse.getUpdatedAt();
        LocalDateTime updatedAt = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(grpcTimestamp.getSeconds(), grpcTimestamp.getNanos()),
                ZoneId.systemDefault() // 시스템 기본 타임존 사용
        );
        medicine.setUpdatedAt(updatedAt);

        // 예약 생성
        Reservation reservation = Reservation.builder()
                .reservationAt(LocalDateTime.now())  // 현재 시간으로 예약
                .pickupDeadline(LocalDateTime.now().plusDays(3))  // 예약 시간에서 3일 후를 픽업 기한으로 설정
                .status("WAIT")  // 상태는 기본값 'WAIT'
                .quantity(request.quantity())
                .member(member)
                .medicine(medicine)
                .build();
        Reservation savedReservation = reservationRepository.save(reservation);

        // TODO: 예약 알림 (약사에게)
        return ReservationResponse.from(savedReservation);
    }

}
