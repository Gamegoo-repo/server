package com.gamegoo.repository.friend;

import com.gamegoo.domain.friend.Friend;
import org.springframework.data.domain.Slice;

import java.util.List;

interface FriendRepositoryCustom {

    Slice<Friend> findFriendsByCursorAndOrdered(Long cursor, Long memberId, Integer pageSize);

    List<Friend> findFriendsByQueryStringAndOrdered(String queryString, Long memberId);

}
