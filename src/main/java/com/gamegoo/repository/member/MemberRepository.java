package com.gamegoo.repository.member;

import com.gamegoo.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAll();

    Optional<Member> findByEmail(String email);

    Optional<Member> findById(Long id);

    @Query("SELECT m FROM Member m INNER JOIN Block b ON m.id = b.blockedMember.id WHERE b.blockerMember.id = " +
            ":blockerId AND b.isDeleted = false ORDER BY b.createdAt DESC")
    Page<Member> findBlockedMembersByBlockerIdAndNotDeleted(@Param("blockerId") Long blockerId, Pageable pageable);

    List<Member> findAllByIdBetween(Long startId, Long endId);

    boolean existsByEmail(String email);

    @Query("SELECT m.mannerScore FROM Member m")
    List<Integer> findAllMannerScores();

    long countByMannerScoreIsNotNullAndBlindFalse();

    long countByMannerScoreGreaterThanAndBlindFalse(Integer mannerScore);

}
