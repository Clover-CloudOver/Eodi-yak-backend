package com.eodi.yak.eodi_yak.domain.member.repository;

import com.eodi.yak.eodi_yak.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
