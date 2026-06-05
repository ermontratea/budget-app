package pl.uczelnia.budgetapp.summary;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.uczelnia.budgetapp.summary.dto.BudgetSummaryResponse;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final SummaryService summaryService;

    @GetMapping
    public BudgetSummaryResponse getSummary() {
        return summaryService.getBudgetSummary();
    }
}