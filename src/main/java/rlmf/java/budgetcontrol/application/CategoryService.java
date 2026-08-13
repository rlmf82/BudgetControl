package rlmf.java.budgetcontrol.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rlmf.java.budgetcontrol.application.ports.in.CategoryUseCase;
import rlmf.java.budgetcontrol.application.ports.out.CategoryRepository;
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
    public List<Category> list() {
        return categoryRepository.findAll();
    }
}
