package rlmf.java.budgetcontrol.adapter.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/{id}")
    public CategoryResponse get(@PathVariable Long id) {
        return CategoryResponse.from(categoryService.get(id));
    }

    @GetMapping
    public List<CategoryResponse> list() {
        return categoryService.list().stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
