package rlmf.java.budgetcontrol.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import rlmf.java.budgetcontrol.domain.model.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OperationRequest(
        @NotNull(message = "type is mandatory")
        OperationType type,

        @NotNull(message = "amount is mandatory")
        BigDecimal amount,

        @NotBlank(message = "description is mandatory")
        @Size(max = 50, message = "description must be at most 50 characters")
        String description,

        @NotNull(message = "categoryId is mandatory")
        Long categoryId,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate date
) {
}
