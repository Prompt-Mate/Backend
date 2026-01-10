package com.tave.PromptMate.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rewrite_results")
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class RewriteResult extends BaseTimeEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name="prompt_id", nullable = true)
    private Prompt prompt;

    @Column(nullable = false, length = 50)
    private String modelName;

    @Column(nullable = false)
    private Integer inputTokens;

    @Column(nullable = false)
    private Integer outputTokens;

    @Column(nullable = false)
    private Long latencyMs;

    @Column(nullable = false)
    private String content;
}
