package com.tave.PromptMate.domain;

import jakarta.persistence.*;
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

    @Column(name = "overall_score", nullable = false)
    private Integer overallScore;

    @Column(name = "clarity_score", nullable = false)
    private Integer clarityScore;

    @Column(name = "specificity_score", nullable = false)
    private Integer specificityScore;

    @Column(name = "structure_score", nullable = false)
    private Integer structureScore;

    @Column(name = "language_score", nullable = false)
    private Integer languageScore;

    @Column(name = "consistency_score", nullable = false)
    private Integer consistencyScore;

    @Lob
    @Column(name = "clarity_comment")
    private String clarityComment;

    @Lob
    @Column(name = "specificity_comment")
    private String specificityComment;

    @Lob
    @Column(name = "structure_comment")
    private String structureComment;

    @Lob
    @Column(name = "language_comment")
    private String languageComment;

    @Lob
    @Column(name = "consistency_comment")
    private String consistencyComment;


    @Column
    private String version;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    // AI 요약 피드백(summary_feedback)을 여기에 저장
    @Lob
    @Column(name = "summary")
    private String summary;

    @Convert(converter = HighlightsConverter.class)
    @Column(name = "highlights", columnDefinition = "json")
    private HighlightsPayload highlights;
}
