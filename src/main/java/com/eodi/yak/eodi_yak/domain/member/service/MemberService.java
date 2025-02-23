package com.eodi.yak.eodi_yak.domain.member.service;

import com.eodi.yak.eodi_yak.domain.member.entity.Member;
import com.eodi.yak.eodi_yak.domain.member.repository.MemberRepository;
import com.eodi.yak.eodi_yak.domain.member.request.MemberSigninRequest;
import com.eodi.yak.eodi_yak.domain.member.request.MemberSignupRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 유저 생성 (회원가입)
    public Member createMember(MemberSignupRequest request) {
        Member member = Member.builder()
                .email(request.userEmail())
                //.password(request.password())
                .password(passwordEncoder.encode(request.password())) // 비밀번호 암호화
                .phoneNumber(request.phone_number())
                .build();

        return memberRepository.save(member);
    }

    // 중복되는 계정(이메일) 존재여부 확인
    public Boolean checkIdDuplicated(String email) {
        return memberRepository.existsByMemberEmail(email);
    }

    // 헤더에서 JWT 토큰 추출
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    // 로그인 (JWT 토큰)
    public Member signin(MemberSigninRequest request) {
        // 이메일로 사용자 조회
        Optional<Member> optionalMember = memberRepository.findByMemberEmail(request.userEmail());

        if (optionalMember.isEmpty()) {
            return null; // 회원 정보 없음
        }

        Member member = optionalMember.get();

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            return null;
        }

        return member;
    }
}
