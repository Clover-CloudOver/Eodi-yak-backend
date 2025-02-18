package com.eodi.yak.eodi_yak.domain.user.entity;

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
    @Column(name = "user_id")
    private Long userId;

    // @Column(name = "user_email", nullable = false)
    // private String userEmail;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Builder
    public Member(String password, String phoneNumber){
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}
