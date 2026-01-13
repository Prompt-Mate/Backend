package com.tave.PromptMate.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(
        name="community_recent",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_community",
                columnNames = {"user_id", "community_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityRecent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 최근 본 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 최근 본 커뮤니티 게시글
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    // 마지막 조회 시간
    @Column(name = "last_viewed_at", nullable = false)
    private LocalDateTime lastViewedAt;

    public static CommunityRecent create(User user, Community community){
        CommunityRecent recent=new CommunityRecent();
        recent.user=user;
        recent.community=community;
        recent.lastViewedAt=LocalDateTime.now();
        return recent;
    }

    // 다시 조회했을 때 시간 갱신
    public void touch(){
        this.lastViewedAt=LocalDateTime.now();
    }

}
