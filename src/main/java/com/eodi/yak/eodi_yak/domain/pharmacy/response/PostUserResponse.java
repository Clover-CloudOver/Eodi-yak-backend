//package com.eodi.yak.eodi_yak.domain.pharmacy.response;
//
//import com.eodi.yak.eodi_yak.domain.user.entity.User;
//import io.swagger.v3.oas.annotations.media.Schema;
//
//public record PostUserResponse(
//        @Schema(description = "사용자 계정 이메일", defaultValue = "test@mail.com")
//        String email,
//
//        @Schema(description = "사용자 전화번호", defaultValue = "010-1234-5678")
//        String phoneNumber
//) {
//    public static User toUser(PostUserResponse request) {
//        //return User.builder()
//    }
//}
