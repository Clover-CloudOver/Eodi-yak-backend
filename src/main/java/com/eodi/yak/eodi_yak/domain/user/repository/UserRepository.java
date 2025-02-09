package com.eodi.yak.eodi_yak.domain.user.repository;

import com.eodi.yak.eodi_yak.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Member, Long> {
}
