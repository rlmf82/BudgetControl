package rlmf.java.budgetcontrol.adapter.out.persistence;

import org.springframework.stereotype.Component;
import rlmf.java.budgetcontrol.adapter.out.persistence.entities.CategoryEntity;
import rlmf.java.budgetcontrol.application.ports.out.CategoryRepository;
import rlmf.java.budgetcontrol.domain.model.Category;

import java.util.List;
import java.util.Optional;

@Component
class CategoryRepositoryAdapter implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;

    CategoryRepositoryAdapter(CategoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Category> findById(Long id) {
        return jpaRepository.findById(id).map(CategoryRepositoryAdapter::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAll().stream()
                .map(CategoryRepositoryAdapter::toDomain)
                .toList();
    }

    static Category toDomain(CategoryEntity entity) {
        return new Category(entity.getId(), entity.getName());
    }
}
