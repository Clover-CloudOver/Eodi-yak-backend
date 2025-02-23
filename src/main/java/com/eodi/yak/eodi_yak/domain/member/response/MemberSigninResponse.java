package com.eodi.yak.eodi_yak.domain.member.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
@Builder
@Schema(description = "로그인 응답")
public record MemberSigninResponse(
        @Schema(description = "jwt 토큰 반환")
        String token
) {}
