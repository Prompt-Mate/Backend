package com.tave.PromptMate.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "community_like",
        uniqueConstraints = {
                @UniqueConstraint(name="uk_community_like_user_community", columnNames = {"user_id", "community_id"})
        },
        indexes = {
                @Index(name = "idx_community_like_user", columnList = "user_id"),
                @Index(name = "idx_community_like_community", columnList = "community_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityLike extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 좋아요 누른 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 좋아요 대상 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @Builder
    private CommunityLike(User user, Community community){
        this.user=user;
        this.community=community;
    }

    public static CommunityLike of(User user, Community community){
        return CommunityLike.builder()
                .user(user)
                .community(community)
                .build();
    }
}
