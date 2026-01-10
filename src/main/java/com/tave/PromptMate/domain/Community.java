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
    @JoinColumn(name = "rewrite_result_id", nullable = false)
    private RewriteResult rewriteResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "prompt_content", nullable = false, columnDefinition = "TEXT")
    private String promptContent;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="visibility", nullable = false, length = 20)
    private Visibility visibility;

    @Column(name = "view_count", nullable = false)
    private long viewCount=0L;


    public static Community create(
            User user,
            Prompt prompt,
            RewriteResult rewriteResult,
            Category category,
            String  title,
            String description,
            String promptContent,
            Visibility visibility
    ){
        Community community=new Community();
        community.user=user;
        community.prompt = prompt;
        community.rewriteResult=rewriteResult;
        community.category=category;
        community.title=title;
        community.description=description;
        community.promptContent=promptContent;
        community.visibility=visibility;
        return community;
    }

    public void hide(){
        this.visibility=Visibility.HIDDEN;
    }

    public void update(String title, String  description, Visibility visibility){
        this.title=title;
        this.description=description;
        this.visibility=visibility;
    }
    public void remove(){
        this.visibility=Visibility.REMOVED;
    }

    public void increaseViewCount(){
        this.viewCount++;
    }

}
