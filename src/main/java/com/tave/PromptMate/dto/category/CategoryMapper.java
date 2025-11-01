package com.tave.PromptMate.dto.category;

import com.tave.PromptMate.domain.Category;

public class CategoryMapper {
    private CategoryMapper(){}

    public static Category toEntity(CreateCategoryRequest req){
        return Category.builder()
                .name(req.name())
                .description(req.description())
                .build();
    }

    public static CategoryResponse toResponse(Category c){
        return new CategoryResponse(
                c.getId(),
                c.getName(),
                c.getDescription()
        );
    }
}
