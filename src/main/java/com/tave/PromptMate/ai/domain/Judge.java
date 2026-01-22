package com.tave.PromptMate.ai.domain;

import com.tave.PromptMate.domain.Prompt;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "judge")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Judge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer overallScore;
    @Column
    private Integer clarityScore;
    @Column
    private Integer specificityScore;
    @Column
    private Integer structureScore;
    @Column
    private Integer languageScore;
    @Column
    private Integer consistencyScore;
    @Column
    private String clarityComment;
    @Column
    private String specificityComment;
    @Column
    private String structureComment;
    @Column
    private String languageComment;
    @Column
    private String consistencyComment;
    @Column
    private String summaryFeedback;

    @Column
    private String originalPrompt;


}
