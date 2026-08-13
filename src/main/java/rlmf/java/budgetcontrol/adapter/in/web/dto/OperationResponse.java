package rlmf.java.budgetcontrol.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import rlmf.java.budgetcontrol.domain.model.Operation;
import rlmf.java.budgetcontrol.domain.model.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OperationResponse(
        Long id,
        OperationType type,
        BigDecimal amount,
        String description,
        Long categoryId,
        String categoryName,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate date
) {

    public static OperationResponse from(Operation operation) {
        return new OperationResponse(
                operation.id(),
                operation.type(),
                operation.amount(),
                operation.description(),
                operation.category().id(),
                operation.category().name(),
                operation.operationDate()
        );
    }
}
