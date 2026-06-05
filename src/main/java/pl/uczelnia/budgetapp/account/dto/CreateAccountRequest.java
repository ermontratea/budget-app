package pl.uczelnia.budgetapp.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
        @NotBlank(message = "Nazwa konta nie może być pusta")
        @Size(max = 40, message = "Za długa nazwa konta, max 40 znaków")
        String name
) {
}
