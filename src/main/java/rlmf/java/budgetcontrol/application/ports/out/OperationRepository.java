package rlmf.java.budgetcontrol.application.ports.out;

import rlmf.java.budgetcontrol.domain.model.Operation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OperationRepository {

    Operation save(Operation operation);

    Optional<Operation> findById(Long id);

    List<Operation> findAll();

    List<Operation> findByDateRange(LocalDate startDate, LocalDate endDate);

    void delete(Operation operation);
}
