package com.gamegoo.dto.member;

import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailCodeRequestDTO {

        @Email(message = "Email 형식이 올바르지 않습니다.")
        private String email;
        private String code;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailRequestDTO {

        @Email(message = "Email 형식이 올바르지 않습니다.")
        @NotBlank(message = "Email은 비워둘 수 없습니다.")
        private String email;
    }

    @Getter
    public static class GameStyleRequestDTO {

        @NotBlank(message = "gameStyleList은 비워둘 수 없습니다.")
        private List<Long> gameStyleIdList;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JoinRequestDTO {

        @Email(message = "Email 형식이 올바르지 않습니다.")
        @NotBlank(message = "Email은 비워둘 수 없습니다.")
        private String email;
        @NotBlank
        private String password;

    }

    @Getter
    @AllArgsConstructor
    public static class PasswordRequestDTO {

        @NotBlank
        private String password;
    }

    @Getter
    public static class PositionRequestDTO {

        @Min(0)
        @Max(5)
        int mainP;
        @Min(0)
        @Max(5)
        int subP;
    }

    @Getter
    public static class ProfileImageRequestDTO {

        String profileImage;
    }

    @Getter
    public static class RefreshTokenRequestDTO {

        @NotBlank
        String refreshToken;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class verifyRiotRequestDTO {

        @NotBlank
        String email;
        @NotBlank
        String gameName;
        @NotBlank
        String tag;
    }
}
