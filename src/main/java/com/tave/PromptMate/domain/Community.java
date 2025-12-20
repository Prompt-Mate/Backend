package com.tave.PromptMate.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="community")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Community extends BaseTimeEntity{

    public enum Visibility{
        PUBLIC,
        HIDDEN, // 관리자/작성자만 보기
        REMOVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id", nullable = false)
    private Prompt prompt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name="visibility", nullable = false, length = 20)
    private Visibility visibility;

    @Column(name="post_id")
    private Long postId;

    public static Community create(
            User user,
            Prompt prompt,
            Category category,
            Visibility visibility
    ){
        Community community=new Community();
        community.user=user;
        community.prompt=prompt;
        community.category=category;
        community.visibility=visibility;
        return community;
    }

    public void hide(){
        this.visibility=Visibility.HIDDEN;
    }
    public void remove(){
        this.visibility=Visibility.REMOVED;
    }
}
