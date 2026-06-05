package pl.uczelnia.budgetapp;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.uczelnia.budgetapp.account.Account;
import pl.uczelnia.budgetapp.account.AccountRepository;
import pl.uczelnia.budgetapp.account.AccountService;
import pl.uczelnia.budgetapp.exceptions.CannotDeleteAccountException;
import pl.uczelnia.budgetapp.transaction.Transaction;
import pl.uczelnia.budgetapp.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldThrowExceptionWhenDeletingAccountWithTransactions() {
        Long accountId = 1L;
        Account testAccount = Account.builder().id(accountId).name("Konto z historią").balance(BigDecimal.ZERO).build();

        given(accountRepository.findById(accountId)).willReturn(Optional.of(testAccount));
        given(transactionRepository.findAllByAccount_Id(accountId)).willReturn(List.of(new Transaction()));

        assertThrows(CannotDeleteAccountException.class, () -> {
            accountService.deleteAccount(accountId);
        });

        then(accountRepository).should(never()).delete(any(Account.class));
    }

    @Test
    void shouldDeleteAccountWhenItHasNoTransactions() {
        Long accountId = 2L;
        Account testAccount = Account.builder().id(accountId).name("Puste Konto").balance(BigDecimal.ZERO).build();

        given(accountRepository.findById(accountId)).willReturn(Optional.of(testAccount));
        given(transactionRepository.findAllByAccount_Id(accountId)).willReturn(List.of());

        accountService.deleteAccount(accountId);

        then(accountRepository).should(times(1)).delete(testAccount);
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFoundById() {
        Long nonExistingId = 999L;
        given(accountRepository.findById(nonExistingId)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            accountService.getAccountById(nonExistingId);
        });
    }
}
