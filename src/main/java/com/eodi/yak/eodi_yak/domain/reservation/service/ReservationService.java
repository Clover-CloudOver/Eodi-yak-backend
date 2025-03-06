package com.eodi.yak.eodi_yak.domain.reservation.service;

import com.eodi.yak.eodi_yak.domain.medicine.repository.MedicineRepository;
import com.eodi.yak.eodi_yak.domain.medicine.entity.Medicine;

// member entity를 reservation entity 파일 안에 복사
import com.eodi.yak.eodi_yak.domain.reservation.entity.Member;
import com.eodi.yak.eodi_yak.domain.reservation.GrpcMemberClient;
import com.eodi.yak.eodi_yak.domain.reservation.request.ReservationRequest;
import com.eodi.yak.eodi_yak.domain.reservation.response.ReservationResponse;
import com.eodi.yak.eodi_yak.domain.reservation.entity.Reservation;
import com.eodi.yak.eodi_yak.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MedicineRepository medicineRepository;
    private final GrpcMemberClient grpcMemberClient;

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
        // gRPC 호출을 통해 사용자 정보 조회
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


        Medicine medicine = medicineRepository.findById_MeNameAndId_PaCode(request.medicineName(), request.pharmacyCode())
                .orElseThrow(() -> new IllegalArgumentException("해당 약을 찾을 수 없습니다."));

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
