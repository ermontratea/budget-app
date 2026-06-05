package pl.uczelnia.budgetapp.account;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.uczelnia.budgetapp.account.dto.AccountResponse;
import pl.uczelnia.budgetapp.account.dto.CreateAccountRequest;
import pl.uczelnia.budgetapp.exceptions.CannotDeleteAccountException;
import pl.uczelnia.budgetapp.transaction.Transaction;
import pl.uczelnia.budgetapp.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts(){
        return accountRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponse createNewAccount(CreateAccountRequest accountDto){
        Account account = Account.builder()
                .name(accountDto.name())
                .balance(BigDecimal.ZERO)
                .build();
        accountRepository.save(account);
        return toResponseDto(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id){
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono konta o ID: " + id));
        return toResponseDto(account);
    }

    @Transactional
    public void deleteAccount(Long id){
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono konta o ID: " + id));
        List<Transaction> transactions = transactionRepository.findAllByAccount_Id(id);
        if (!transactions.isEmpty()){
            throw new CannotDeleteAccountException("Konto ma przypisane transakcje, nie można usunąć");
        }
        accountRepository.delete(account);
    }



    public AccountResponse toResponseDto(Account account){
        return new AccountResponse(account.getId(), account.getName(), account.getBalance());
    }

}
