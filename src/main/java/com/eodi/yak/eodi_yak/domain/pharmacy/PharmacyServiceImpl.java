package com.eodi.yak.eodi_yak.domain.pharmacy;

import com.eodi.yak.eodi_yak.domain.pharmacy.entity.Pharmacy;
import com.eodi.yak.eodi_yak.domain.pharmacy.repository.PharmacyRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pharmacy.PharmacyOuterClass;
import pharmacy.PharmacyServiceGrpc;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class PharmacyServiceImpl extends PharmacyServiceGrpc.PharmacyServiceImplBase {
    private final PharmacyRepository pharmacyRepository;

    @Override
    public void findNearbyPharmacies(PharmacyOuterClass.PharmacyNearRequest request, StreamObserver<PharmacyOuterClass.PageResponse> responseObserver) {
        double latitude = Double.parseDouble(request.getLatitude());
        double longitude = Double.parseDouble(request.getLongitude());
        int radius = request.getRadius();
        int page = request.getPageable().getPage();
        int size = request.getPageable().getSize();
        Pageable pageable = PageRequest.of(page, size);

        Page<Pharmacy> pages = pharmacyRepository.findPharmaciesWithinRadius(latitude, longitude, radius, pageable);

        // Pharmacy를 PharmacyProtobuf로 변환
        List<PharmacyOuterClass.Pharmacy> pharmacies = pages.getContent().stream()
                .map(this::toProtoPharmacy)  // Pharmacy 객체를 Protobuf Pharmacy로 변환
                .collect(Collectors.toList());

        // PageResponse를 Protobuf 형식으로 빌드
        PharmacyOuterClass.PageResponse response = PharmacyOuterClass.PageResponse.newBuilder()
                .addAllResults(pharmacies)   // Protobuf Pharmacy 객체들을 결과로 추가
                .setTotalItems((int) pages.getTotalElements())  // 페이지의 총 아이템 수
                .build();

        // 응답 전송
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // Pharmacy를 Protobuf Pharmacy 객체로 변환
    private PharmacyOuterClass.Pharmacy toProtoPharmacy(Pharmacy pharmacy) {
        return PharmacyOuterClass.Pharmacy.newBuilder()
                .setPaCode(pharmacy.getPaCode())
                .setPaName(pharmacy.getPaName())
                .setAddress(pharmacy.getAddress())
                .setLatitude(String.valueOf(pharmacy.getLatitude()))
                .setLongitude(String.valueOf(pharmacy.getLongitude()))
                .setPhoneNumber(pharmacy.getPhoneNumber())
                .setEmail(pharmacy.getEmail())
                .build();
    }


    @Override
    public void findNearbyId(PharmacyOuterClass.PharmacyIdRequest request, StreamObserver<PharmacyOuterClass.Pharmacy> responseObserver) {
        String paCode = request.getPaCode();

        Pharmacy pharmacy = pharmacyRepository.findById(paCode)
            .orElseGet(() -> {
                responseObserver.onError(
                        io.grpc.Status.NOT_FOUND.withDescription("pharmacy not found").asRuntimeException()
                );
                return null;
            });
        if (pharmacy == null) {
            return;  // 이미 에러를 반환했으므로, 이 시점 이후 코드 실행은 하지 않음
        }

        // 응답 객체 생성
        PharmacyOuterClass.Pharmacy response = PharmacyOuterClass.Pharmacy.newBuilder()
                .setPaCode(pharmacy.getPaCode())
                .setPaName(pharmacy.getPaName())
                .setAddress(pharmacy.getAddress())
                .setLatitude(String.valueOf(pharmacy.getLatitude()))
                .setLongitude(String.valueOf(pharmacy.getLongitude()))
                .setPhoneNumber(pharmacy.getPhoneNumber())
                .setEmail(pharmacy.getEmail())
                .build();

        // 클라이언트에게 응답 전송
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}