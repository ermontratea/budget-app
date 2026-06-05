package pl.uczelnia.budgetapp.transaction;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.uczelnia.budgetapp.account.Account;
import pl.uczelnia.budgetapp.account.AccountRepository;
import pl.uczelnia.budgetapp.exceptions.IncorrectTransactionAmountException;
import pl.uczelnia.budgetapp.transaction.dto.CreateTransactionRequest;
import pl.uczelnia.budgetapp.transaction.dto.TransactionResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private static final Map<String, BigDecimal> CATEGORY_LIMITS = Map.of(
            "jedzenie", new BigDecimal("500.00"),
            "rozrywka", new BigDecimal("300.00")
    );
    private static final BigDecimal DEFAULT_LIMIT = new BigDecimal("1000.00");

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request){
        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono konta o ID: " + request.accountId()));

        if (request.type() == TransactionType.EXPENSE){
            account.setBalance(account.getBalance().subtract(request.amount()));
        }else if(request.type()==TransactionType.INCOME){
            account.setBalance(account.getBalance().add(request.amount()));
        }
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0){
            throw new IncorrectTransactionAmountException("Kwota transakcji musi być większa od zera.");
        }

        String warningMessage = null;
        if (request.type() == TransactionType.EXPENSE) {
            BigDecimal limit = CATEGORY_LIMITS.getOrDefault(request.category().toLowerCase(), DEFAULT_LIMIT);

            BigDecimal alreadySpent = Optional.ofNullable(transactionRepository.sumExpensesBySpecificCategory(request.category()))
                    .orElse(BigDecimal.ZERO);

            BigDecimal totalSpentWithNew = alreadySpent.add(request.amount());

            if (totalSpentWithNew.compareTo(limit) > 0) {
                warningMessage = String.format(
                        "'%s' - Limit: %s zł, wydane: %s zł.",
                        request.category(), limit, totalSpentWithNew
                );
            }
        }
        Transaction transaction = Transaction.builder()
                .amount(request.amount())
                .type(request.type())
                .category(request.category())
                .description(request.description())
                .date(LocalDateTime.now())
                .account(account)
                .build();

        transactionRepository.save(transaction);
        return toResponseDto(transaction, warningMessage);
    }


    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji o ID: " + id));

        Account account = transaction.getAccount();

        if (transaction.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        } else if (transaction.getType() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        }
        transactionRepository.delete(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getFilteredTransactions(String category, LocalDate from, LocalDate to) {

        LocalDateTime fromDate = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDate = (to != null) ? to.atTime(23, 59, 59) : null;

        return transactionRepository.findFilteredTransactions(category, fromDate, toDate).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public String exportTransactionsToCsv(Long accountId) {

        accountRepository.findById(accountId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Nie znaleziono konta o ID: " + accountId));

        List<Transaction> transactions = transactionRepository.findAllByAccount_Id(accountId);

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID;Data;Typ;Kategoria;Kwota;Opis\n");

        for (Transaction t : transactions) {
            csvBuilder.append(t.getId()).append(";")
                    .append(t.getDate()).append(";")
                    .append(t.getType()).append(";")
                    .append(t.getCategory()).append(";")
                    .append(t.getAmount()).append(";")
                    .append(t.getDescription() != null ? t.getDescription() : "").append("\n");
        }

        return csvBuilder.toString();
    }


    private TransactionResponse toResponseDto(Transaction transaction, String warning) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getCategory(),
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getAccount().getId(),
                warning
        );
    }
    private TransactionResponse toResponseDto(Transaction transaction) {
        return toResponseDto(transaction, null);
    }
}
