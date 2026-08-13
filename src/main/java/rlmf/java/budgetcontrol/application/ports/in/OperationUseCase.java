package rlmf.java.budgetcontrol.application.ports.in;

import rlmf.java.budgetcontrol.application.OperationCommand;
import rlmf.java.budgetcontrol.domain.model.Operation;

import java.util.List;

public interface OperationUseCase {

    Operation create(OperationCommand command);

    Operation update(Long id, OperationCommand command);

    void delete(Long id);

    Operation get(Long id);

    List<Operation> list();
}
