package com.gamegoo.converter;

import com.gamegoo.domain.friend.Friend;
import com.gamegoo.domain.member.Member;
import com.gamegoo.dto.member.MemberResponse;
import com.gamegoo.dto.member.MemberResponse.friendInfoDTO;
import com.gamegoo.util.MemberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Collectors;

public class MemberConverter {

    public static MemberResponse.blockListDTO toBlockListDTO(Page<Member> blockList) {
        List<MemberResponse.blockedMemberDTO> blockedMemberDtoList = blockList
                .stream()
                .map(MemberConverter::toBlockedMemberDTO)
                .collect(Collectors.toList());

        return MemberResponse.blockListDTO.builder()
                .blockedMemberDTOList(blockedMemberDtoList)
                .listSize(blockedMemberDtoList.size())
                .totalPage(blockList.getTotalPages())
                .totalElements(blockList.getTotalElements())
                .isFirst(blockList.isFirst())
                .isLast(blockList.isLast())
                .build();
    }

    public static MemberResponse.blockedMemberDTO toBlockedMemberDTO(Member member) {
        return MemberResponse.blockedMemberDTO.builder()
                .memberId(member.getId())
                .profileImg(member.getProfileImage())
                .email(member.getEmail())
                .name(member.getBlind() ? "(탈퇴한 사용자)" : member.getGameName())
                .isBlind(member.getBlind())
                .build();
    }

    public static MemberResponse.myProfileMemberDTO toMyProfileDTO(Member member) {
        List<MemberResponse.GameStyleResponseDTO> gameStyleResponseDTOList = null;
        if (member.getMemberGameStyleList() != null) {
            gameStyleResponseDTOList = member.getMemberGameStyleList()
                    .stream()
                    .map(memberGameStyle ->
                            MemberResponse.GameStyleResponseDTO.builder()
                                    .gameStyleId(memberGameStyle.getGameStyle().getId())
                                    .gameStyleName(memberGameStyle.getGameStyle().getStyleName())
                                    .build())
                    .collect(Collectors.toList());
        }

        List<MemberResponse.ChampionResponseDTO> championResponseDTOList = null;
        if (member.getMemberChampionList() != null) {
            championResponseDTOList = member.getMemberChampionList().stream()
                    .map(memberChampion ->
                            MemberResponse.ChampionResponseDTO.builder()
                                    .championId(memberChampion.getChampion().getId())
                                    .championName(memberChampion.getChampion().getName())
                                    .build())
                    .collect(Collectors.toList());
        }

        return MemberResponse.myProfileMemberDTO.builder()
                .id(member.getId())
                .mike(member.getMike())
                .email(member.getEmail())
                .gameName(member.getGameName())
                .tag(member.getTag())
                .tier(member.getTier())
                .rank(member.getRank())
                .profileImg(member.getProfileImage())
                .manner(member.getMannerLevel())
                .mainP(member.getMainPosition())
                .subP(member.getSubPosition())
                .wantP(member.getWantPosition())
                .isAgree(member.getIsAgree())
                .isBlind(member.getBlind())
                .winrate(member.getWinRate())
                .loginType(String.valueOf(member.getLoginType()))
                .updatedAt(String.valueOf(member.getUpdatedAt()))
                .gameStyleResponseDTOList(gameStyleResponseDTOList)
                .championResponseDTOList(championResponseDTOList)
                .build();
    }

    public static MemberResponse.myProfileDTO profileDTO(Member member, Double mannerScoreRank) {
        List<MemberResponse.GameStyleResponseDTO> gameStyleResponseDTOList = null;
        if (member.getMemberGameStyleList() != null) {
            gameStyleResponseDTOList = member.getMemberGameStyleList()
                    .stream()
                    .map(memberGameStyle -> MemberResponse.GameStyleResponseDTO.builder()
                            .gameStyleId(memberGameStyle.getGameStyle().getId())
                            .gameStyleName(memberGameStyle.getGameStyle().getStyleName())
                            .build())
                    .collect(Collectors.toList());
        }

        List<MemberResponse.ChampionResponseDTO> championResponseDTOList = null;
        if (member.getMemberChampionList() != null) {
            championResponseDTOList = member.getMemberChampionList()
                    .stream()
                    .map(memberChampion -> MemberResponse.ChampionResponseDTO.builder()
                            .championId(memberChampion.getChampion().getId())
                            .championName(memberChampion.getChampion().getName())
                            .build())
                    .collect(Collectors.toList());
        }

        return MemberResponse.myProfileDTO.builder()
                .id(member.getId())
                .mike(member.getMike())
                .email(member.getEmail())
                .gameName(member.getGameName())
                .tag(member.getTag())
                .tier(member.getTier())
                .rank(member.getRank())
                .profileImg(member.getProfileImage())
                .mannerLevel(member.getMannerLevel())
                .mannerRank(mannerScoreRank)
                .mainP(member.getMainPosition())
                .subP(member.getSubPosition())
                .wantP(member.getWantPosition())
                .isAgree(member.getIsAgree())
                .isBlind(member.getBlind())
                .winrate(member.getWinRate())
                .loginType(String.valueOf(member.getLoginType()))
                .updatedAt(String.valueOf(member.getUpdatedAt()))
                .gameStyleResponseDTOList(gameStyleResponseDTOList)
                .championResponseDTOList(championResponseDTOList)
                .build();
    }

    public static MemberResponse.memberProfileDTO toMemberProfileDTO(Member member,
                                                                     Member targetMember, Boolean isFriend,
                                                                     Long friedRequestMemberId, Double mannerScoreRank,
                                                                     Long mannerRatingCount) {
        List<MemberResponse.GameStyleResponseDTO> gameStyleResponseDTOList = null;
        if (targetMember.getMemberGameStyleList() != null) {
            gameStyleResponseDTOList = targetMember.getMemberGameStyleList()
                    .stream()
                    .map(memberGameStyle -> MemberResponse.GameStyleResponseDTO.builder()
                            .gameStyleId(memberGameStyle.getGameStyle().getId())
                            .gameStyleName(memberGameStyle.getGameStyle().getStyleName())
                            .build())
                    .collect(Collectors.toList());
        }

        List<MemberResponse.ChampionResponseDTO> championResponseDTOList = null;
        if (targetMember.getMemberChampionList() != null) {
            championResponseDTOList = targetMember.getMemberChampionList()
                    .stream()
                    .map(memberChampion -> MemberResponse.ChampionResponseDTO.builder()
                            .championId(memberChampion.getChampion().getId())
                            .championName(memberChampion.getChampion().getName())
                            .build())
                    .collect(Collectors.toList());
        }

        return MemberResponse.memberProfileDTO.builder()
                .id(targetMember.getId())
                .mike(targetMember.getMike())
                .gameName(targetMember.getGameName())
                .tag(targetMember.getTag())
                .tier(targetMember.getTier())
                .rank(targetMember.getRank())
                .profileImg(targetMember.getProfileImage())
                .mannerLevel(targetMember.getMannerLevel())
                .mannerRank(mannerScoreRank)
                .mannerRatingCount(mannerRatingCount)
                .mainP(targetMember.getMainPosition())
                .wantP(targetMember.getWantPosition())
                .subP(targetMember.getSubPosition())
                .isAgree(targetMember.getIsAgree())
                .isBlind(targetMember.getBlind())
                .winrate(targetMember.getWinRate())
                .loginType(String.valueOf(targetMember.getLoginType()))
                .updatedAt(String.valueOf(targetMember.getUpdatedAt()))
                .blocked(MemberUtils.isBlocked(member, targetMember))
                .friend(isFriend)
                .friendRequestMemberId(friedRequestMemberId)
                .gameStyleResponseDTOList(gameStyleResponseDTOList)
                .championResponseDTOList(championResponseDTOList)
                .build();
    }

    public static MemberResponse.friendListDTO toFriendListDTO(Slice<Friend> friends) {
        List<friendInfoDTO> friendInfoDTOList = friends
                .stream()
                .map(MemberConverter::toFriendInfoDto)
                .collect(Collectors.toList());

        return MemberResponse.friendListDTO.builder()
                .friendInfoDTOList(friendInfoDTOList)
                .list_size(friendInfoDTOList.size())
                .has_next(friends.hasNext())
                .next_cursor(
                        friends.hasNext()
                                ? friends.getContent().get(friendInfoDTOList.size() - 1).getToMember().getId()
                                : null)
                .build();
    }

    public static MemberResponse.friendInfoDTO toFriendInfoDto(Friend friend) {
        return MemberResponse.friendInfoDTO.builder()
                .memberId(friend.getToMember().getId())
                .name(
                        friend.getToMember().getBlind()
                                ? "(탈퇴한 사용자)"
                                : friend.getToMember().getGameName())
                .memberProfileImg(friend.getToMember().getProfileImage())
                .isLiked(friend.getIsLiked())
                .isBlind(friend.getToMember().getBlind())
                .build();
    }

}
