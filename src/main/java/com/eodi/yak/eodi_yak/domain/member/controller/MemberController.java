package com.eodi.yak.eodi_yak.domain.member.controller;

import com.eodi.yak.eodi_yak.domain.member.entity.Member;
import com.eodi.yak.eodi_yak.domain.member.request.MemberSignupRequest;
import com.eodi.yak.eodi_yak.domain.member.response.MemberSignupResponse;
import com.eodi.yak.eodi_yak.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member")
@Tag(name = "Member", description = "어디약?! 회원 관련 API")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 유저 생성 (회원가입)
    // TODO: 회원가입 (JWT 토큰이용)
    @PostMapping("/signup")
    @Operation(summary = "사용자 회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<MemberSignupResponse> createMember(@RequestBody MemberSignupRequest request) {
        Member member = memberService.createMember(request);
        MemberSignupResponse response = new MemberSignupResponse(member.getMemberId());
        return ResponseEntity.ok(response);
    }

}
