package com.tave.PromptMate.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prompt")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Prompt extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;


}
