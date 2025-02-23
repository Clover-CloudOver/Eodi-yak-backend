package com.eodi.yak.eodi_yak.domain.member.repository;

import com.eodi.yak.eodi_yak.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByMemberEmail(String email);
    Optional<Member> findByMemberEmail(String email);
}
