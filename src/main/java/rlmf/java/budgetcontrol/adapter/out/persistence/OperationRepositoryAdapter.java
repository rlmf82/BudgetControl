package rlmf.java.budgetcontrol.adapter.out.persistence;

import org.springframework.stereotype.Component;
import rlmf.java.budgetcontrol.adapter.out.persistence.entities.CategoryEntity;
import rlmf.java.budgetcontrol.adapter.out.persistence.entities.OperationEntity;
import rlmf.java.budgetcontrol.application.ports.out.OperationRepository;
import rlmf.java.budgetcontrol.domain.model.Category;
import rlmf.java.budgetcontrol.domain.model.Operation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
class OperationRepositoryAdapter implements OperationRepository {

    private final OperationJpaRepository operationJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;

    OperationRepositoryAdapter(OperationJpaRepository operationJpaRepository, CategoryJpaRepository categoryJpaRepository) {
        this.operationJpaRepository = operationJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public Operation save(Operation operation) {
        CategoryEntity categoryEntity = categoryJpaRepository.getReferenceById(operation.category().id());
        OperationEntity entity = operation.id() != null
                ? operationJpaRepository.findById(operation.id())
                        .orElseThrow(() -> new IllegalStateException("Operation not found: " + operation.id()))
                : new OperationEntity(operation.type(), operation.amount(), operation.description(), categoryEntity, operation.operationDate());

        entity.setType(operation.type());
        entity.setAmount(operation.amount());
        entity.setDescription(operation.description());
        entity.setCategory(categoryEntity);
        entity.setOperationDate(operation.operationDate());

        return toDomain(operationJpaRepository.save(entity));
    }

    @Override
    public Optional<Operation> findById(Long id) {
        return operationJpaRepository.findById(id).map(OperationRepositoryAdapter::toDomain);
    }

    @Override
    public List<Operation> findAll() {
        return operationJpaRepository.findAll().stream()
                .map(OperationRepositoryAdapter::toDomain)
                .toList();
    }

    @Override
    public List<Operation> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return operationJpaRepository.findByOperationDateBetweenOrderByOperationDateAsc(startDate, endDate).stream()
                .map(OperationRepositoryAdapter::toDomain)
                .toList();
    }

    @Override
    public void delete(Operation operation) {
        operationJpaRepository.deleteById(operation.id());
    }

    private static Operation toDomain(OperationEntity entity) {
        Category category = CategoryRepositoryAdapter.toDomain(entity.getCategory());
        return new Operation(entity.getId(), entity.getType(), entity.getAmount(), entity.getDescription(), category, entity.getOperationDate());
    }
}
