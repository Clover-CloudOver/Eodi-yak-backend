package com.eodi.yak.eodi_yak.domain.user.service;

import com.eodi.yak.eodi_yak.domain.user.entity.Member;
import com.eodi.yak.eodi_yak.domain.user.repository.UserRepository;
import com.eodi.yak.eodi_yak.domain.user.request.UserSignupRequest;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 유저 생성 (회원가입)
    public Member createUser(UserSignupRequest request) {
        Member member = Member.builder()
                .password(request.password())
                .phoneNumber(request.phone_number())
                .build();

        return userRepository.save(member);
    }
}
