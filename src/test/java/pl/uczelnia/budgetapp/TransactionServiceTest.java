package pl.uczelnia.budgetapp;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.uczelnia.budgetapp.account.Account;
import pl.uczelnia.budgetapp.account.AccountRepository;
import pl.uczelnia.budgetapp.transaction.Transaction;
import pl.uczelnia.budgetapp.transaction.TransactionRepository;
import pl.uczelnia.budgetapp.transaction.TransactionService;
import pl.uczelnia.budgetapp.transaction.TransactionType;
import pl.uczelnia.budgetapp.transaction.dto.CreateTransactionRequest;
import pl.uczelnia.budgetapp.transaction.dto.TransactionResponse;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(1L)
                .name("Konto Testowe")
                .balance(new BigDecimal("1000.00"))
                .build();
    }

    @Test
    void shouldCreateIncomeTransactionAndIncreaseBalance() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                new BigDecimal("200.00"),
                TransactionType.INCOME,
                "Wynagrodzenie",
                "Premia",
                1L
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals(new BigDecimal("1200.00"), testAccount.getBalance());
        assertNull(response.warning());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void shouldCreateExpenseTransactionAndTriggerBudgetWarning() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                new BigDecimal("600.00"),
                TransactionType.EXPENSE,
                "Jedzenie",
                "Drogie zakupy",
                1L
        );

        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(transactionRepository.sumExpensesBySpecificCategory("Jedzenie")).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.createTransaction(request);

        assertEquals(new BigDecimal("400.00"), testAccount.getBalance());
        assertNotNull(response.warning());
        assertTrue(response.warning().contains("Limit: "));
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                new BigDecimal("50.00"),
                TransactionType.EXPENSE,
                "Inne",
                "Opis",
                999L
        );

        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            transactionService.createTransaction(request);
        });

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void shouldDeleteExpenseTransactionAndRevertBalance() {

        Transaction testTransaction = Transaction.builder()
                .id(10L)
                .amount(new BigDecimal("150.00"))
                .type(TransactionType.EXPENSE)
                .category("Rozrywka")
                .account(testAccount)
                .build();

        when(transactionRepository.findById(10L)).thenReturn(Optional.of(testTransaction));

        transactionService.deleteTransaction(10L);

        assertEquals(new BigDecimal("1150.00"), testAccount.getBalance());
        verify(transactionRepository, times(1)).delete(testTransaction);
    }
}
