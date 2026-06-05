package pl.uczelnia.budgetapp.exceptions;

public class IncorrectTransactionAmountException extends RuntimeException {
    public IncorrectTransactionAmountException(String message) {
        super(message);
    }
}
