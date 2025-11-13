package com.tave.PromptMate.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evaluation")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evaluation extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prompt_id")
    private Prompt prompt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rewrite_id")
    private RewriteResult rewriteResult;

    @Column(nullable = false)
    private Integer clarity;
    @Column(nullable = false)
    private Integer specificity;
    @Column(nullable = false)
    private Integer context;
    @Column(nullable = false)
    private Integer creativity;

    @Column(nullable = false)
    private String advice;

    @Column
    private String version;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "total_score", nullable = false)
    @Min(0) @Max(100)
    private Integer totalScore;

    @Lob
    @Column(name = "summary")
    private String summary;

    @Convert(converter = HighlightsConverter.class)
    @Column(name = "highlights", columnDefinition = "json")
    private HighlightsPayload highlights;
}
