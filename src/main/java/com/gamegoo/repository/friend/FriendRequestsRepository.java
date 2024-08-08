package com.gamegoo.repository.friend;

import com.gamegoo.domain.Member;
import com.gamegoo.domain.friend.FriendRequestStatus;
import com.gamegoo.domain.friend.FriendRequests;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRequestsRepository extends JpaRepository<FriendRequests, Long> {

    Optional<FriendRequests> findByFromMemberAndToMemberAndStatus(Member fromMember,
        Member toMember, FriendRequestStatus status);

}
