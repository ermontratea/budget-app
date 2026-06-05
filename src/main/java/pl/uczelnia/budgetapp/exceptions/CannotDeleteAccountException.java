package pl.uczelnia.budgetapp.exceptions;

public class CannotDeleteAccountException extends RuntimeException {
    public CannotDeleteAccountException(String message) {
        super(message);
    }
}
