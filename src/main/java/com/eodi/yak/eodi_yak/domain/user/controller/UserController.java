package com.eodi.yak.eodi_yak.domain.user.controller;

import com.eodi.yak.eodi_yak.domain.user.entity.Member;
import com.eodi.yak.eodi_yak.domain.user.request.UserSignupRequest;
import com.eodi.yak.eodi_yak.domain.user.response.UserSignupResponse;
import com.eodi.yak.eodi_yak.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "어디약?! 회원 관련 API")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 유저 생성 (회원가입)
    @PostMapping("/signup")
    @Operation(summary = "사용자 회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<UserSignupResponse> createUser(@RequestBody UserSignupRequest request) {
        Member member = userService.createUser(request);
        UserSignupResponse response = new UserSignupResponse(member.getUserId());
        return ResponseEntity.ok(response);
    }

    // 로그인
    //@PostMapping("/login")
    //public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {}
}
