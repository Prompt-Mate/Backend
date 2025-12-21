package com.tave.PromptMate.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 255)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    private String password;      //자체회원가입용 (카카오는 null)

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
}
