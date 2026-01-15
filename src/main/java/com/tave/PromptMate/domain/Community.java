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
    @JoinColumn(name = "rewrite_result_id", nullable = true)
    private RewriteResult rewriteResult;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 30)
    private Platform platform;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private PromptCategory category;

    @Column(length=500)
    private String imageUrl;


    public static Community create(
            User user,
            RewriteResult rewriteResult,
            String  title,
            String description,
            String promptContent,
            Visibility visibility,
            Platform platform,
            PromptCategory category,
            String imageUrl
    ){
        Community community=new Community();
        community.user=user;
        community.rewriteResult=rewriteResult;
        community.title=title;
        community.description=description;
        community.promptContent=promptContent;
        community.visibility=visibility;
        community.platform=platform;
        community.category=category;
        community.imageUrl=imageUrl;
        return community;
    }

    public void hide(){
        this.visibility=Visibility.HIDDEN;
    }

    public void update(String title, String  description, Visibility visibility){
        this.title=title;
        this.description=description;
        this.visibility=visibility;
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
    }
    public void remove(){
        this.visibility=Visibility.REMOVED;
    }

    public void increaseViewCount(){
        this.viewCount++;
    }

}
