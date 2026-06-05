package pl.uczelnia.budgetapp.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.uczelnia.budgetapp.account.dto.AccountResponse;
import pl.uczelnia.budgetapp.account.dto.CreateAccountRequest;
import pl.uczelnia.budgetapp.transaction.TransactionService;


import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping
    public List<AccountResponse> getAllAccounts(){
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public AccountResponse getAccountById(@PathVariable Long id){
        return accountService.getAccountById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createNewAccount(@Valid @RequestBody CreateAccountRequest accountDto){

        return accountService.createNewAccount(accountDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long id){
        accountService.deleteAccount(id);
    }

    @GetMapping("/{id}/transactions/export")
    public ResponseEntity<byte[]> exportTransactions(@PathVariable Long id) {
        String csvData = transactionService.exportTransactionsToCsv(id);

        byte[] fileBytes = csvData.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "transactions_account_" + id + ".csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileBytes);
    }
}
