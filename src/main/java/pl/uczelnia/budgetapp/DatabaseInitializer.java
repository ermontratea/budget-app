package pl.uczelnia.budgetapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.uczelnia.budgetapp.account.Account;
import pl.uczelnia.budgetapp.account.AccountRepository;
import pl.uczelnia.budgetapp.transaction.TransactionService;
import pl.uczelnia.budgetapp.transaction.TransactionType;
import pl.uczelnia.budgetapp.transaction.dto.CreateTransactionRequest;

import java.math.BigDecimal;

@Configuration
public class DatabaseInitializer {

    @Bean
    CommandLineRunner initDatabase(AccountRepository accountRepository, TransactionService transactionService) {
        return args -> {

            if (accountRepository.count() == 0) {

                Account account1 = Account.builder()
                        .name("Konto Osobiste")
                        .balance(BigDecimal.ZERO)
                        .build();
                accountRepository.save(account1);

                transactionService.createTransaction(new CreateTransactionRequest(
                        new BigDecimal("5000.00"), TransactionType.INCOME, "Wynagrodzenie", "Wypłata za maj", account1.getId()
                ));

                transactionService.createTransaction(new CreateTransactionRequest(
                        new BigDecimal("120.50"), TransactionType.EXPENSE, "Jedzenie", "Zakupy w Biedronce", account1.getId()
                ));

                transactionService.createTransaction(new CreateTransactionRequest(
                        new BigDecimal("250.00"), TransactionType.EXPENSE, "Jedzenie", "Obiad w restauracji", account1.getId()
                ));

                transactionService.createTransaction(new CreateTransactionRequest(
                        new BigDecimal("80.00"), TransactionType.EXPENSE, "Rozrywka", "Bilety do kina", account1.getId()
                ));


                Account account2 = Account.builder()
                        .name("Konto Oszczędnościowe")
                        .balance(BigDecimal.ZERO)
                        .build();
                accountRepository.save(account2);
                transactionService.createTransaction(new CreateTransactionRequest(
                        new BigDecimal("1000.00"),
                        TransactionType.INCOME,
                        "Oszczędności",
                        "Bilans otwarcia konta oszczędnościowego",
                        account2.getId()
                ));
                System.out.println("Zainicjalizowana przykładowa baza danych");

            }
        };
    }
}
