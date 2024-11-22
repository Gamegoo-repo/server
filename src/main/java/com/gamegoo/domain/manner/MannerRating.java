package com.gamegoo.domain.manner;

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
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MannerRating extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manner_rating_id", nullable = false)
    private Long id;

    @OneToMany(mappedBy = "mannerRating", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MannerRatingKeyword> mannerRatingKeywordList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id", nullable = false)
    private Member fromMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id", nullable = false)
    private Member toMember;

    @Column(name = "is_Positive")
    private Boolean isPositive;

    // 연관관계 메소드
    public void setToMember(Member toMember) {
        if (this.toMember != null) {
            this.toMember.getMannerRatingList().remove(this);
        }
        this.toMember = toMember;
        if (toMember != null) {
            this.toMember.getMannerRatingList().add(this);
        }
    }

    public void removeMannerRatingKeyword(MannerRatingKeyword mannerRatingKeyword) {
        this.mannerRatingKeywordList.remove(mannerRatingKeyword);
        mannerRatingKeyword.setMannerRating(null);
    }

}
