package com.eodi.yak.eodi_yak.domain.reservation;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
//import com.eodi.yak.eodi_yak.domain.reservation.request.ReservationRequest;

import member.Member;
import member.MemberServiceGrpc;
import org.springframework.stereotype.Component;

@Component
public class GrpcMemberClient {
    private final MemberServiceGrpc.MemberServiceBlockingStub stub;

    public GrpcMemberClient() {
        ManagedChannel channel = NettyChannelBuilder.forAddress("member", 9090)
                .usePlaintext()
                .build();
        stub = MemberServiceGrpc.newBlockingStub(channel);
    }

    public Member.MemberResponse getMemberId(Member.MemberRequest request) {
        return stub.getMemberById(request);
    }
}
