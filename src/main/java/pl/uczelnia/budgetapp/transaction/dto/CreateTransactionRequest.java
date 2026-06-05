package pl.uczelnia.budgetapp.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pl.uczelnia.budgetapp.transaction.TransactionType;
import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotNull(message = "Kwota jest wymagana")
        @Positive(message = "Kwota musi być większa od zera")
        BigDecimal amount,
        @NotNull(message = "Typ transakcji (INCOME/EXPENSE) jest wymagany")
        TransactionType type,
        @NotBlank(message = "Kategoria nie może być pusta")
        String category,
        String description,
        @NotNull(message = "Identyfikator konta jest wymagany")
        Long accountId
) {}
