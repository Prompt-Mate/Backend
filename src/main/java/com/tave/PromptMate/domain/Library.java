package com.tave.PromptMate.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "library")
public class Library {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rewrite_result_id", nullable = false)
    private RewriteResult rewriteResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="community_id",nullable = true)
    private Community community;

    @Column(name = "saved_title", length = 200)
    private String savedTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromptCategory category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void attachCommunityAndUpdateTitle(Community community, String savedTitle) {
        this.community = community;
        this.savedTitle = savedTitle;
    }

}
