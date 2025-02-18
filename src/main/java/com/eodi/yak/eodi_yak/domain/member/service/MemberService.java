package com.eodi.yak.eodi_yak.domain.member.service;

import com.eodi.yak.eodi_yak.domain.member.entity.Member;
import com.eodi.yak.eodi_yak.domain.member.repository.MemberRepository;
import com.eodi.yak.eodi_yak.domain.member.request.MemberSignupRequest;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 유저 생성 (회원가입)
    public Member createMember(MemberSignupRequest request) {
        Member member = Member.builder()
                .password(request.password())
                .phoneNumber(request.phone_number())
                .build();

        return memberRepository.save(member);
    }
}
