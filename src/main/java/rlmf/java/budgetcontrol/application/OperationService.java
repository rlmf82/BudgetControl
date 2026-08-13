package rlmf.java.budgetcontrol.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rlmf.java.budgetcontrol.application.ports.in.OperationUseCase;
import rlmf.java.budgetcontrol.application.ports.out.CategoryRepository;
import rlmf.java.budgetcontrol.application.ports.out.OperationRepository;
import rlmf.java.budgetcontrol.domain.ResourceNotFoundException;
import rlmf.java.budgetcontrol.domain.model.Category;
import rlmf.java.budgetcontrol.domain.model.Operation;
import rlmf.java.budgetcontrol.domain.service.OperationRules;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional
public class OperationService implements OperationUseCase {

    public static final ZoneId PORTUGAL_ZONE = ZoneId.of("Europe/Lisbon");

    private final OperationRepository operationRepository;
    private final CategoryRepository categoryRepository;

    public OperationService(OperationRepository operationRepository, CategoryRepository categoryRepository) {
        this.operationRepository = operationRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Operation create(OperationCommand command) {
        Category category = findCategory(command.categoryId());
        Operation operation = OperationRules.createNew(command.type(), command.amount(), command.description(), category, command.date(), today());
        return operationRepository.save(operation);
    }

    @Override
    public Operation update(Long id, OperationCommand command) {
        Operation existing = findOperation(id);
        Category category = findCategory(command.categoryId());
        Operation updated = OperationRules.update(existing, command.type(), command.amount(), command.description(), category, command.date(), today());
        return operationRepository.save(updated);
    }

    @Override
    public void delete(Long id) {
        operationRepository.delete(findOperation(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Operation get(Long id) {
        return findOperation(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Operation> list() {
        return operationRepository.findAll();
    }

    private LocalDate today() {
        return LocalDate.now(PORTUGAL_ZONE);
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    private Operation findOperation(Long id) {
        return operationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found: " + id));
    }
}
