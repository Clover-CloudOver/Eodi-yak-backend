package com.eodi.yak.eodi_yak.domain.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "회원가입 응답")
public record MemberSignupResponse(
    @Schema(description = "사용자 id 반환")
    Long id
) {}
