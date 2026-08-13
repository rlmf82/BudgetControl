package rlmf.java.budgetcontrol.adapter.in.web.dto;

import rlmf.java.budgetcontrol.domain.model.CategoryTotal;

import java.math.BigDecimal;
import java.util.List;

public record CategoryTotalResponse(
        Long categoryId,
        String categoryName,
        BigDecimal totalIncome,
        BigDecimal totalPayments,
        List<OperationResponse> operations
) {

    public static CategoryTotalResponse from(CategoryTotal categoryTotal) {
        return new CategoryTotalResponse(
                categoryTotal.category().id(),
                categoryTotal.category().name(),
                categoryTotal.totalIncome(),
                categoryTotal.totalPayments(),
                categoryTotal.operations().stream().map(OperationResponse::from).toList()
        );
    }
}
