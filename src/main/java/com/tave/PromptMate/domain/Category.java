package com.tave.PromptMate.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category")
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 300)
    private String description;

    public void changeName(String name) {
        this.name = name;
    }

    public void changeDescription(String description) {
        this.description = description;
    }
}
