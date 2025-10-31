package com.tave.PromptMate.domain.library;

import com.tave.PromptMate.domain.Prompt;
import com.tave.PromptMate.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="library")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LibraryItem {

    @EmbeddedId
    private LibraryId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id")
    private User user;

    @MapsId("promptId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="prompt_id")
    private Prompt prompt;

    @Column(name="saved_title", nullable = false, length = 200)
    private String savedTitle;

    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @PrePersist
    void onCreate() { if (createAt==null) createAt=LocalDateTime.now();}

    public static LibraryItem of(User user, Prompt prompt, String savedTitle){
        return LibraryItem.builder()
                .id(new LibraryId(user.getId(), prompt.getId()))
                .user(user)
                .prompt(prompt)
                .savedTitle(savedTitle)
                .build();
    }
}
