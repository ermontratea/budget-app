package pl.uczelnia.budgetapp.summary.dto;

import java.math.BigDecimal;
import java.util.List;

public record BudgetSummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        List<CategorySpending> categorySpendings
) {}
