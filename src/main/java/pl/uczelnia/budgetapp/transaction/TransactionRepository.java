package pl.uczelnia.budgetapp.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.uczelnia.budgetapp.summary.dto.CategorySpending;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    List<Transaction> findAllByAccount_Name(String accountName);
    List<Transaction> findAllByAccount_Id(Long id);
    List<Transaction> findAllByType(TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(CAST(:category AS string) IS NULL OR LOWER(t.category) = LOWER(CAST(:category AS string))) AND " +
            "(CAST(:fromDate AS localdatetime) IS NULL OR t.date >= :fromDate) AND " +
            "(CAST(:toDate AS localdatetime) IS NULL OR t.date <= :toDate)")
    List<Transaction> findFilteredTransactions(@Param("category") String category,
                                               @Param("fromDate") LocalDateTime fromDate,
                                               @Param("toDate") LocalDateTime toDate);


    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = pl.uczelnia.budgetapp.transaction.TransactionType.INCOME")
    BigDecimal sumTotalIncome();

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = pl.uczelnia.budgetapp.transaction.TransactionType.EXPENSE")
    BigDecimal sumTotalExpenses();

    @Query("SELECT new pl.uczelnia.budgetapp.summary.dto.CategorySpending(t.category, SUM(t.amount)) " +
            "FROM Transaction t " +
            "WHERE t.type = pl.uczelnia.budgetapp.transaction.TransactionType.EXPENSE " +
            "GROUP BY t.category")
    List<CategorySpending> getExpensesGroupedByCategory();

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.type = pl.uczelnia.budgetapp.transaction.TransactionType.EXPENSE " +
            "AND LOWER(t.category) = LOWER(:category)")
    BigDecimal sumExpensesBySpecificCategory(@Param("category") String category);

}
