package com.gamegoo.repository.chat;

import com.gamegoo.domain.chat.Chat;
import com.gamegoo.domain.chat.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long>, ChatRepositoryCustom {

    Optional<Chat> findFirstByChatroomIdOrderByCreatedAtDesc(Long chatroomId);

    Optional<Chat> findByChatroomAndTimestamp(Chatroom chatroom, Long timestamp);

}
