package com.eodi.yak.eodi_yak.domain.member.controller;

import com.eodi.yak.eodi_yak.domain.auth.JWTUtil;
import com.eodi.yak.eodi_yak.domain.member.entity.Member;
import com.eodi.yak.eodi_yak.domain.member.request.MemberSigninRequest;
import com.eodi.yak.eodi_yak.domain.member.request.MemberSignupRequest;
import com.eodi.yak.eodi_yak.domain.member.response.MemberSigninResponse;
import com.eodi.yak.eodi_yak.domain.member.response.MemberSignupResponse;
import com.eodi.yak.eodi_yak.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Tag(name = "Member", description = "어디약?! 회원 관련 API")
public class MemberController {
    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    // 회원가입 (JWT 토큰 이용)
    @PostMapping("/signup")
    @Operation(summary = "사용자 회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<MemberSignupResponse> createMember(@RequestBody MemberSignupRequest request) {
        // ID 중복 확인
        if (memberService.checkIdDuplicated(request.userEmail())) {
            return ResponseEntity.badRequest().build();
        }

        // 계정 생성
        memberService.createMember(request);
        return ResponseEntity.ok().build();
    }

    // 로그인 (JWT 토큰 이용)
    @PostMapping("/signin")
    @Operation(summary = "사용자 로그인", description = "사용자가 로그인을 시도합니다.")
    public ResponseEntity<MemberSigninResponse> createMember(@RequestBody MemberSigninRequest request) {
        Member member = memberService.signin(request);
        if (member == null) {
            return ResponseEntity.badRequest().build();
        }

        String token = jwtUtil.createJwt(member.getMemberId().toString(), 1000 * 60 * 60L);

        MemberSigninResponse response = new MemberSigninResponse(token);
        return ResponseEntity.ok(response);
    }
}
