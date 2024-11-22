package com.gamegoo.dto.chat;

import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ChatRequest {

    @Getter
    public static class ChatroomCreateRequest {

        @NotNull
        Long targetMemberId;

        String postUrl;

    }

    @Getter
    public static class ChatroomStartRequest {

        @NotNull
        Long targetMemberId;

        String postUrl;

    }

    @Getter
    public static class ChatroomCreateByMatchRequest {

        @NotNull
        List<Long> memberList;

    }

    @Getter
    public static class ChatCreateRequest {

        @NotEmpty
        String message;
        @Valid
        SystemFlagRequest system;

    }

    @Getter
    public static class SystemFlagRequest {

        @Min(1)
        @Max(2)
        @NotNull
        Integer flag;

        @NotNull
        Long boardId;

    }

}
