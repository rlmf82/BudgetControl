package rlmf.java.budgetcontrol.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rlmf.java.budgetcontrol.application.ports.in.CategoryUseCase;
import rlmf.java.budgetcontrol.application.ports.out.CategoryRepository;
import rlmf.java.budgetcontrol.domain.ResourceNotFoundException;
import rlmf.java.budgetcontrol.domain.model.Category;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService implements CategoryUseCase {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category get(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    @Override
    public List<Category> list() {
        return categoryRepository.findAll();
    }
}
