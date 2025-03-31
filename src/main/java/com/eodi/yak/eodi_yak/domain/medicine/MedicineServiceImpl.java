package com.eodi.yak.eodi_yak.domain.medicine;

import com.eodi.yak.eodi_yak.domain.medicine.repository.MedicineRepository;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import medicine.Medicine;
import medicine.MedicineServiceGrpc;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;


@GrpcService
@RequiredArgsConstructor
public class MedicineServiceImpl extends MedicineServiceGrpc.MedicineServiceImplBase {
    private final MedicineRepository medicineRepository;

    private String nvl(String value) {
        return value != null ? value : "";
    }

    @Override
    public void getMedicineById(Medicine.MedicineRequest request, StreamObserver<Medicine.MedicineResponse> responseObserver) {
        String medicineName = request.getMedicineName();
        String pharmacyCode = request.getPharmacyCode();

        // database에서 조회
        com.eodi.yak.eodi_yak.domain.medicine.entity.Medicine medicine = medicineRepository.findById_MeNameAndId_PaCode(medicineName, pharmacyCode)
                .orElseGet(() -> {
                    responseObserver.onError(
                            io.grpc.Status.NOT_FOUND.withDescription("Medicine not found").asRuntimeException()
                    );
                    return null;
                });

        if (medicine == null) {
            return;  // 이미 에러를 반환했으므로, 이 시점 이후 코드 실행은 하지 않음
        }

        // LocalDateTime -> Timestamp로 변환
        ZonedDateTime zonedDateTime = medicine.getUpdatedAt().atZone(ZoneOffset.UTC); // UTC로 변환
        Timestamp updatedAtTimestamp = Timestamp.newBuilder()
                .setSeconds(zonedDateTime.toEpochSecond()) // 초 단위로 변환
                .setNanos(zonedDateTime.getNano()) // 나노초 단위로 변환
                .build();

        // 응답 객체 생성
        Medicine.MedicineResponse response = Medicine.MedicineResponse.newBuilder()
                .setMedicineName(medicine.getId().getMeName())
                .setPharmacyCode(medicine.getId().getPaCode())
                .setStock(medicine.getStock())
                .setUpdatedAt(updatedAtTimestamp)
                .setPharmacyAddress(medicine.getPharmacy().getAddress())
                .setPharmacyLatitude(String.valueOf(medicine.getPharmacy().getLatitude()))
                .setPharmacyLongitude(String.valueOf(medicine.getPharmacy().getLongitude()))
                .setPharmacyPhoneNumber(medicine.getPharmacy().getPhoneNumber())
                .setPharmacyEmail(nvl(medicine.getPharmacy().getEmail()))
                .build();

        // 클라이언트에게 응답 전송
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}