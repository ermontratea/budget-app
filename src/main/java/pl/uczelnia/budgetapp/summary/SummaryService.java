package pl.uczelnia.budgetapp.summary;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.uczelnia.budgetapp.summary.dto.BudgetSummaryResponse;
import pl.uczelnia.budgetapp.summary.dto.CategorySpending;
import pl.uczelnia.budgetapp.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public BudgetSummaryResponse getBudgetSummary() {
        BigDecimal totalIncome = Optional.ofNullable(transactionRepository.sumTotalIncome())
                .orElse(BigDecimal.ZERO);

        BigDecimal totalExpenses = Optional.ofNullable(transactionRepository.sumTotalExpenses())
                .orElse(BigDecimal.ZERO);

        List<CategorySpending> categorySpendings = transactionRepository.getExpensesGroupedByCategory();

        return new BudgetSummaryResponse(totalIncome, totalExpenses, categorySpendings);
    }
}