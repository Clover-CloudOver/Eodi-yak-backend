package com.eodi.yak.eodi_yak.domain.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "로그인 요청")
public record MemberSigninRequest(
    @Schema(description = "사용자 이메일", defaultValue = "test@test.com")
    String userEmail,

    @Schema(description = "사용자 비밀번호", defaultValue = "1234")
    String password
) {}
