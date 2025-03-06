package com.eodi.yak.eodi_yak.domain.member;

import com.eodi.yak.eodi_yak.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import member.Member;
import member.Member.MemberRequest;
import member.MemberServiceGrpc;
import net.devh.boot.grpc.server.service.GrpcService;
import io.grpc.stub.StreamObserver;


@GrpcService
@RequiredArgsConstructor
public class MemberServiceImpl extends MemberServiceGrpc.MemberServiceImplBase {
    private final MemberRepository memberRepository;

    @Override
    public void getMemberById(MemberRequest request, StreamObserver<Member.MemberResponse> responseObserver) {
        String memberId = request.getMemberId();

        // database에서 조회
        com.eodi.yak.eodi_yak.domain.member.entity.Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 응답 객체 생성
        Member.MemberResponse response = Member.MemberResponse.newBuilder()
                .setMemberId(String.valueOf(member.getMemberId()))
                .setEmail(member.getMemberEmail())
                .setPhoneNumber(member.getPhoneNumber())
                .build();

        // 클라이언트에게 응답 전송
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}