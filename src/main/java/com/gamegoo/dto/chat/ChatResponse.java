package com.gamegoo.dto.chat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

public class ChatResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatroomViewListDTO {

        List<ChatroomViewDTO> chatroomViewDTOList;
        Integer list_size;
        Boolean has_next;
        Long next_cursor;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatroomViewDTO {

        Long chatroomId;
        String uuid;
        Long targetMemberId;
        Integer targetMemberImg;
        String targetMemberName;
        Boolean friend;
        Boolean blocked;
        Boolean blind;
        Long friendRequestMemberId;
        String lastMsg;
        String lastMsgAt;
        Integer notReadMsgCnt;
        Long lastMsgTimestamp;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonPropertyOrder({
            "uuid",
            "memberId",
            "gameName",
            "memberProfileImg",
            "friend",
            "blocked",
            "blind",
            "friendRequestMemberId",
            "system",
            "chatMessageList"
    })
    public static class ChatroomEnterDTO {

        String uuid;
        Long memberId;
        String gameName;
        Integer memberProfileImg;
        Boolean friend;
        Boolean blocked;
        Boolean blind;
        Long friendRequestMemberId;
        SystemFlagDTO system;
        ChatMessageListDTO chatMessageList;

    }

    @SuperBuilder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageListDTO {

        List<ChatMessageDTO> chatMessageDtoList;
        Integer list_size;
        Boolean has_next;
        Long next_cursor;

    }

    @SuperBuilder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageDTO {

        Long senderId;
        String senderName;
        Integer senderProfileImg;
        String message;
        String createdAt;
        Long timestamp;

    }

    @SuperBuilder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemMessageDTO extends ChatMessageDTO {

        Integer systemType;
        Long boardId;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatCreateResultDTO {

        Long senderId;
        String senderName;
        Integer senderProfileImg;
        String message;
        String createdAt;
        Long timestamp;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemFlagDTO {

        Integer flag;
        Long boardId;

    }

}
