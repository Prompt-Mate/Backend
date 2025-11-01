package com.tave.PromptMate.service;

import com.tave.PromptMate.common.NotFoundException;
import com.tave.PromptMate.domain.Category;
import com.tave.PromptMate.dto.category.CategoryMapper;
import com.tave.PromptMate.dto.category.CategoryResponse;
import com.tave.PromptMate.dto.category.CreateCategoryRequest;
import com.tave.PromptMate.repository.CategoryRepository;
import com.tave.PromptMate.repository.PromptRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PromptRepository promptRepository;

    // 카테고리 생성하기
    public CategoryResponse create(CreateCategoryRequest req){
        if(categoryRepository.existsByName(req.name())){
            throw new IllegalStateException("duplicated category name: "+ req.name());
        }
        Category saved = categoryRepository.save(CategoryMapper.toEntity(req));
        return CategoryMapper.toResponse(saved);
    }

    // 카테고리 단건 조회
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("category not found: "+categoryId));
        return CategoryMapper.toResponse(category);
    }

    // 카테고리 전체 목록
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll(){
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    // 카테고리 수정하기
    public CategoryResponse update(Long categoryId, String newName, String newDescription){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("category not found: " + categoryId));

        if (newName != null && !newName.equals(category.getName())){
            if (categoryRepository.existsByName(newName)){
                throw new IllegalStateException("duplicated category name: "+ newName);
            }
            category.changeName(newName);
        }
        if (newDescription != null){
            category.changeDescription(newDescription);
        }

        return CategoryMapper.toResponse(category);
    }

    // 카테고리 삭제
    public void delete(Long categoryId){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("category not found: "+ categoryId));
        long promptCount = promptRepository.countByCategory(category);
        if (promptCount > 0){
            throw new IllegalStateException("cannot delete category on use(prompts: "+ promptCount+")");
        }
        categoryRepository.delete(category);
    }
}
