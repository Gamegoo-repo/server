package com.gamegoo.repository.member;

import com.gamegoo.domain.member.Member;
import com.gamegoo.domain.member.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByMember(Member member);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
