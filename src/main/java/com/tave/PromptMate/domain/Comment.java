package com.tave.PromptMate.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity{

    public enum Status{
        NORMAL,
        REMOVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 게시글에 달린 댓글인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="community_id", nullable = false)
    private Community community;

    // 누가 댓글 작성했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 답글이면 부모 댓글이 존재, 일반 댓글이면 null
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // 부모 댓글 기준 답글 목록
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> replies= new ArrayList<>();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    public static Comment create(Community community, User user, Comment parent, String content){
        Comment c= new Comment();
        c.community=community;
        c.user=user;
        c.parent=parent;
        c.content=content;
        c.status=Status.NORMAL;
        return c;
    }

    public void updateContent(String content){
        this.content=content;
    }

    public void remove(){
        this.status=Status.REMOVED;
        this.content="삭제된 댓글입니다.";
    }

    public boolean isRemoved(){
        return this.status==Status.REMOVED;
    }

    public void setParent(Comment parent){
        this.parent=parent;
        if(parent!=null){
            parent.replies.add(this);
        }
    }
}
