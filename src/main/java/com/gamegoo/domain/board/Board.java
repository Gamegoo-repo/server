package com.gamegoo.domain.board;

import com.gamegoo.domain.common.BaseDateTimeEntity;
import com.gamegoo.domain.member.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Board")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Board extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", nullable = false)
    private Long id;

    @Column(name = "mode", nullable = false)
    private Integer mode;

    @Column(name = "main_position")
    private Integer mainPosition;

    @Column(name = "sub_position")
    private Integer subPosition;

    @Column(name = "want_position")
    private Integer wantPosition;

    @Column(name = "mike")
    private Boolean mike = false;

    @Column(name = "content", length = 5000)
    private String content;

    @Column(name = "board_profile_image")
    private Integer boardProfileImage;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardGameStyle> boardGameStyles = new ArrayList<>();

    // 연관관계 메소드
    public void setMember(Member member) {
        if (this.member != null) {
            this.member.getBoardList().remove(this);
        }
        this.member = member;
        if (member != null) {
            this.member.getBoardList().add(this);
        }
    }

    public void updateBoard(Integer mode, Integer mainPosition, Integer subPosition, Integer wantPosition, Boolean mike,
                            String content, Integer boardProfileImage) {
        this.mode = mode;
        this.mainPosition = mainPosition;
        this.subPosition = subPosition;
        this.wantPosition = wantPosition;
        this.mike = mike;
        this.content = content;
        this.boardProfileImage = boardProfileImage;
    }

    public void removeBoardGameStyle(BoardGameStyle boardGameStyle) {
        this.boardGameStyles.remove(boardGameStyle);
        boardGameStyle.setBoard(null);
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}

