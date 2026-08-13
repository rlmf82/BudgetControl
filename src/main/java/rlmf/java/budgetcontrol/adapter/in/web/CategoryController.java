package rlmf.java.budgetcontrol.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rlmf.java.budgetcontrol.adapter.in.web.dto.CategoryResponse;
import rlmf.java.budgetcontrol.application.ports.in.CategoryUseCase;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryUseCase categoryService;

    public CategoryController(CategoryUseCase categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> list() {
        return categoryService.list().stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
