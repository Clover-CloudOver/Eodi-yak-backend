package com.eodi.yak.eodi_yak.domain.reservation;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import medicine.Medicine;
import medicine.MedicineServiceGrpc;
import org.springframework.stereotype.Component;

@Component
public class GrpcMedicineClient {
    private final MedicineServiceGrpc.MedicineServiceBlockingStub stub;

    public GrpcMedicineClient() {
        ManagedChannel channel = NettyChannelBuilder.forAddress("medicine.eodi-yak.svc.cluster.local", 50052)
                .usePlaintext()
                .build();
        stub = MedicineServiceGrpc.newBlockingStub(channel);
    }

    public Medicine.MedicineResponse getMedicineById(Medicine.MedicineRequest request) {
        return stub.getMedicineById(request);
    }

}
