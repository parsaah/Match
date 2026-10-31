package org.example.match.community.mapper;

import org.example.match.community.dto.CategoryCreateRequest;
import org.example.match.community.dto.CategoryResponse;
import org.example.match.community.dto.CategoryUpdateRequest;
import org.example.match.community.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryCreateRequest request);

    CategoryResponse toResponse(Category category);

    void updateEntity(
            CategoryUpdateRequest request,
            @MappingTarget Category category
    );
}