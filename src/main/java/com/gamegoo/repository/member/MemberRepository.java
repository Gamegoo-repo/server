package com.gamegoo.repository.member;

import com.gamegoo.domain.Member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    Optional<Member> findByGameName(String gameName);

    Optional<Member> findById(Long id);

    Optional<Member> findByRefreshToken(String refresh_token);

    @Query("SELECT m FROM Member m INNER JOIN Block b ON m.id = b.blockedMember.id WHERE b.blockerMember.id = :blockerId AND m.blind = false ORDER BY b.createdAt DESC")
    Page<Member> findBlockedMembersByBlockerIdAndNotBlind(@Param("blockerId") Long blockerId, Pageable pageable);


    boolean existsByEmail(String email);
}
