package rlmf.java.budgetcontrol.adapter.in.web.dto;

import rlmf.java.budgetcontrol.domain.model.Category;

public record CategoryResponse(Long id, String name) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.id(), category.name());
    }
}
