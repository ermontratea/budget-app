package pl.uczelnia.budgetapp.account.dto;

import pl.uczelnia.budgetapp.account.Account;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String name,
        BigDecimal balance
) {}
