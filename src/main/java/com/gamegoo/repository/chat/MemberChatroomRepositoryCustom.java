package com.gamegoo.repository.chat;

import com.gamegoo.domain.chat.MemberChatroom;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MemberChatroomRepositoryCustom {

    Slice<MemberChatroom> findActiveMemberChatroomByCursorOrderByLastChat(Long memberId, Long cursor, Integer pageSize);

    List<MemberChatroom> findAllActiveMemberChatroom(Long memberId);

}
