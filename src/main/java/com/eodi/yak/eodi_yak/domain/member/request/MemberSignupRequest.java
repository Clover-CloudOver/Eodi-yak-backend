package com.eodi.yak.eodi_yak.domain.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "회원가입 요청")
public record MemberSignupRequest(
         @Schema(description = "사용자 이메일", defaultValue = "test@test.com")
         String userEmail,

        @Schema(description = "사용자 비밀번호", defaultValue = "1234")
        String password,

        @Schema(description = "사용자 전화번호", defaultValue = "010-1234-5678")
        String phone_number
) {}
