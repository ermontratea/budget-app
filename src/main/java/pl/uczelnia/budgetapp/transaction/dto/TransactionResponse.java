package pl.uczelnia.budgetapp.transaction.dto;

import pl.uczelnia.budgetapp.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        TransactionType type,
        String category,
        String description,
        LocalDateTime date,
        Long accountId,
        String warning
) {
}
