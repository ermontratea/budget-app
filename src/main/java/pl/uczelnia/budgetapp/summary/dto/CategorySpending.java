package pl.uczelnia.budgetapp.summary.dto;

import java.math.BigDecimal;

public record CategorySpending(
        String category,
        BigDecimal totalAmount
) {}
