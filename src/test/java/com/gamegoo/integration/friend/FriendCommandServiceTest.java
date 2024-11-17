package com.gamegoo.integration.friend;

import com.gamegoo.apiPayload.code.status.ErrorStatus;
import com.gamegoo.apiPayload.exception.GeneralException;
import com.gamegoo.domain.board.Board;
import com.gamegoo.domain.friend.Friend;
import com.gamegoo.domain.member.LoginType;
import com.gamegoo.domain.member.Member;
import com.gamegoo.repository.board.BoardRepository;
import com.gamegoo.repository.friend.FriendRepository;
import com.gamegoo.repository.member.MemberRepository;
import com.gamegoo.service.member.FriendService;
import com.gamegoo.service.member.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@Transactional
public class FriendCommandServiceTest {

    @Autowired
    private FriendService friendService;

    @SpyBean
    private ProfileService profileService;  // 실제 ProfileService를 Spy로 감싸기

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    private Member member1;
    private Member member2;
    private Member member3;
    private Member member4;
    private Member member5;
    private Member blindMember;
    private Member systemMember;
    private Board board2;

    @BeforeEach
    public void setMember() {
        // 기본 테스트에 사용할 멤버 객체를 미리 생성
        int randomProfileImage = ThreadLocalRandom.current().nextInt(1, 9);
        member1 = Member.builder()
                .id(1L)
                .email("test@mail.com")
                .password("12345678")
                .gameName("member1")
                .loginType(LoginType.GENERAL)
                .profileImage(randomProfileImage)
                .blind(false)
                .mike(false)
                .mannerLevel(1)
                .isAgree(true)
                .blockList(new ArrayList<>())
                .memberChatroomList(new ArrayList<>())
                .boardList(new ArrayList<>())
                .build();

        member2 = Member.builder()
                .id(2L)
                .email("test2@mail.com")
                .gameName("member2")
                .password("12345678")
                .loginType(LoginType.GENERAL)
                .profileImage(randomProfileImage)
                .blind(false)
                .mike(false)
                .mannerLevel(1)
                .isAgree(true)
                .blockList(new ArrayList<>())
                .memberChatroomList(new ArrayList<>())
                .boardList(new ArrayList<>())
                .build();

        member3 = Member.builder()
                .id(3L)
                .email("test3@mail.com")
                .gameName("member3")
                .password("12345678")
                .loginType(LoginType.GENERAL)
                .profileImage(randomProfileImage)
                .blind(false)
                .mike(false)
                .mannerLevel(1)
                .isAgree(true)
                .blockList(new ArrayList<>())
                .memberChatroomList(new ArrayList<>())
                .boardList(new ArrayList<>())
                .build();

        member4 = Member.builder()
                .id(4L)
                .email("test4@mail.com")
                .password("12345678")
                .gameName("member4")
                .loginType(LoginType.GENERAL)
                .profileImage(randomProfileImage)
                .blind(false)
                .mike(false)
                .mannerLevel(1)
                .isAgree(true)
                .blockList(new ArrayList<>())
                .memberChatroomList(new ArrayList<>())
                .boardList(new ArrayList<>())
                .build();
        member5 = Member.builder()
                .id(5L)
                .email("test5@mail.com")
                .password("12345678")
                .gameName("member5")
                .loginType(LoginType.GENERAL)
                .profileImage(randomProfileImage)
                .blind(false)
                .mike(false)
                .mannerLevel(1)
                .isAgree(true)
                .blockList(new ArrayList<>())
                .memberChatroomList(new ArrayList<>())
                .boardList(new ArrayList<>())
                .build();

        blindMember = Member.builder()
                .id(1000L)
                .email("blind@mail.com")
                .password("12345678")
                .loginType(LoginType.GENERAL)
                .profileImage(randomProfileImage)
                .blind(true)
                .mike(false)
                .mannerLevel(1)
                .isAgree(true)
                .blockList(new ArrayList<>())
                .memberChatroomList(new ArrayList<>())
                .build();

        systemMember = Member.builder()
                .id(0L)
                .email("system@mail.com")
                .password("12345678")
                .loginType(LoginType.GENERAL)
                .profileImage(randomProfileImage)
                .blind(false)
                .mike(false)
                .mannerLevel(1)
                .isAgree(true)
                .blockList(new ArrayList<>())
                .memberChatroomList(new ArrayList<>())
                .build();

        member1 = memberRepository.save(this.member1);
        member2 = memberRepository.save(this.member2);
        member3 = memberRepository.save(this.member3);
        member4 = memberRepository.save(this.member4);
        member5 = memberRepository.save(this.member5);
        blindMember = memberRepository.save(this.blindMember);
        systemMember = memberRepository.save(this.systemMember);

        board2 = Board.builder()
                .mode(1)
                .mainPosition(1)
                .subPosition(1)
                .wantPosition(1)
                .mike(true)
                .boardGameStyles(new ArrayList<>())
                .content("content")
                .boardProfileImage(1)
                .deleted(false)
                .build();
        board2.setMember(member2);
        board2 = boardRepository.save(board2);

        // findSystemMember 메서드만 mock 처리
        doReturn(systemMember).when(profileService).findSystemMember();
    }

    /**
     * member1과 나머지 회원들 간 친구 관계 설정
     */
    private void setFriends() {
        ArrayList<Member> targetMemberList = new ArrayList<>();
        targetMemberList.add(member2);
        targetMemberList.add(member3);
        targetMemberList.add(member4);
        targetMemberList.add(member5);

        targetMemberList.forEach(targetMember -> {
            Friend memberFriend = Friend.builder()
                    .fromMember(member1)
                    .toMember(targetMember)
                    .isLiked(false)
                    .build();

            Friend targetMemberFriend = Friend.builder()
                    .fromMember(targetMember)
                    .toMember(member1)
                    .isLiked(false)
                    .build();

            friendRepository.save(memberFriend);
            friendRepository.save(targetMemberFriend);
        });
    }

    @Nested
    @DisplayName("친구 목록 조회")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class getFriends {

        @Nested
        @DisplayName("성공 케이스")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class SuccessCase {
            @Test
            @Order(1)
            @DisplayName("1. 친구가 10명 이하인 경우")
            public void getFriendsSucceeds() throws Exception {
                // given
                setFriends();

                // when
                Slice<Friend> friends1 = friendService.getFriends(member1.getId(), null);
                Slice<Friend> friends2 = friendService.getFriends(member2.getId(), null);

                // then
                // member1의 친구 목록 size 검증
                assertEquals(4, friends1.getContent().size());

                // member2의 친구 목록 size 검증
                assertEquals(1, friends2.getContent().size());

                // member1의 친구 목록 오름차순 정렬 검증
                assertEquals(member2, friends1.getContent().get(0).getToMember());
                assertEquals(member5, friends1.getContent().get(3).getToMember());
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class FailCase {

            @Test
            @Order(2)
            @DisplayName("2. cursorId로 음수를 입력한 경우")
            public void getFriendsFailedWhenCursorIdIsNegative() throws Exception {
                // given
                ErrorStatus expectedErrorCode = ErrorStatus.CURSOR_INVALID;

                // when
                GeneralException exception = assertThrows(GeneralException.class, () -> {
                    friendService.getFriends(member1.getId(), -1L);
                });

                // then
                assertEquals(expectedErrorCode, exception.getCode());

            }
        }

    }

    @Nested
    @DisplayName("소환사명으로 친구 검색")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class searchFriendByQueryString {
        @Nested
        @DisplayName("성공 케이스")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class SuccessCase {
            @Test
            @Order(3)
            @DisplayName("3. 검색어와 일치하는 친구가 1명인 경우")
            public void searchFriendByQueryStringSucceeds() throws Exception {
                // given
                setFriends();

                // when
                List<Friend> friends = friendService.searchFriendByQueryString(member1.getId(), "member2");

                // then
                assertEquals(1, friends.size());
            }

            @Test
            @Order(4)
            @DisplayName("4. 검색어와 일치하는 친구가 여러명인 경우")
            public void searchFriendsByQueryStringSucceeds() throws Exception {
                // given
                setFriends();

                // when
                List<Friend> friends = friendService.searchFriendByQueryString(member1.getId(), "member");

                // then
                // 검색 결과 size 검증
                assertEquals(4, friends.size());

                // 검색 결과 오름차순 정렬 여부 검증
                assertEquals(member2, friends.get(0).getToMember());
                assertEquals(member5, friends.get(3).getToMember());
            }

            @Test
            @Order(5)
            @DisplayName("5. 검색어와 일치하는 친구가 없는 경우")
            public void searchFriendByQueryStringSucceedsWhenNoResult() throws Exception {
                // given

                // when
                List<Friend> friends = friendService.searchFriendByQueryString(member1.getId(), "member");

                // then
                assertEquals(0, friends.size());
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
        class FailCase {
            @Test
            @Order(6)
            @DisplayName("6. 검색어 string이 100자를 초과하는 경우")
            public void searchFriendsByQueryStringFailed() throws Exception {
                // given
                ErrorStatus expectedErrorCode = ErrorStatus.FRIEND_SEARCH_QUERY_BAD_REQUEST;

                String queryString = "membermembermembermembermembermembermembermembermembermembermembermembermembermembermembermembermembermembermembermember";

                // when
                GeneralException exception = assertThrows(GeneralException.class, () -> {
                    friendService.searchFriendByQueryString(member1.getId(), queryString);
                });

                // then
                assertEquals(expectedErrorCode, exception.getCode());

            }
        }
    }
}
