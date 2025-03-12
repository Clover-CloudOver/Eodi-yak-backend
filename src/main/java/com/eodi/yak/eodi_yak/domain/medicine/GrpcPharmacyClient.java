package com.eodi.yak.eodi_yak.domain.medicine;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.springframework.stereotype.Component;
import pharmacy.PharmacyOuterClass;
import pharmacy.PharmacyServiceGrpc;

@Component
public class GrpcPharmacyClient {
    private final PharmacyServiceGrpc.PharmacyServiceBlockingStub stub;

    public GrpcPharmacyClient() {
        ManagedChannel channel = NettyChannelBuilder.forAddress("pharmacy.default.svc.cluster.local", 9090)
                .usePlaintext()
                .build();
        stub = PharmacyServiceGrpc.newBlockingStub(channel);
    }

    public PharmacyOuterClass.PageResponse findNearbyPharmacies(PharmacyOuterClass.PharmacyNearRequest request) {
        return stub.findNearbyPharmacies(request);
    }

    public PharmacyOuterClass.Pharmacy findNearbyId(PharmacyOuterClass.PharmacyIdRequest request) {
        return stub.findNearbyId(request);
    }
}
