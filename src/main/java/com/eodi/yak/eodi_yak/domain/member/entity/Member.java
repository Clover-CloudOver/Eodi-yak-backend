package com.eodi.yak.eodi_yak.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Builder
    public Member(String email, String password, String phoneNumber){
        this.userEmail = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}
