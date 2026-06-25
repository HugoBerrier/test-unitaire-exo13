package com.example.bank.service;

import com.example.bank.dto.CreateAccountRequest;
import com.example.bank.exception.AccountAlreadyExistsException;
import com.example.bank.exception.AccountNotFoundException;
import com.example.bank.exception.InsufficientFundsException;
import com.example.bank.exception.InvalidAmountException;
import com.example.bank.model.Account;
import com.example.bank.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private DefaultAccountService accountService;

    // --- Création de compte ---

    @Test
    void shouldCreateAccountSuccessfully() {
        // Arrange
        CreateAccountRequest request = new CreateAccountRequest();
        request.setNumber("ACC001");
        request.setHolder("Alice");
        when(accountRepository.existsByNumber("ACC001")).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Account created = accountService.createAccount(request);

        // Assert
        assertEquals("ACC001", created.getNumber());
        assertEquals("Alice", created.getHolder());
        assertEquals(0, created.getBalance());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldRejectAccountWithDuplicateNumber() {
        // Arrange
        CreateAccountRequest request = new CreateAccountRequest();
        request.setNumber("ACC001");
        request.setHolder("Alice");
        when(accountRepository.existsByNumber("ACC001")).thenReturn(true);

        // Act & Assert
        assertThrows(AccountAlreadyExistsException.class, () -> accountService.createAccount(request));
    }

    // --- Consultation ---

    @Test
    void shouldReturnExistingAccount() {
        // Arrange
        Account account = new Account("ACC001", "Alice", 100);
        when(accountRepository.findByNumber("ACC001")).thenReturn(Optional.of(account));

        // Act
        Account found = accountService.getAccount("ACC001");

        // Assert
        assertEquals("ACC001", found.getNumber());
        assertEquals("Alice", found.getHolder());
        assertEquals(100, found.getBalance());
    }

    @Test
    void shouldThrowErrorWhenAccountNotFound() {
        // Arrange
        when(accountRepository.findByNumber("ACC999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class, () -> accountService.getAccount("ACC999"));
    }

    @Test
    void shouldReturnAllAccounts() {
        // Arrange
        List<Account> accounts = List.of(
                new Account("ACC001", "Alice", 100),
                new Account("ACC002", "Bob", 200)
        );
        when(accountRepository.findAll()).thenReturn(accounts);

        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertEquals(2, result.size());
        assertEquals("ACC001", result.get(0).getNumber());
        assertEquals("ACC002", result.get(1).getNumber());
    }

    // --- Dépôt ---

    @Test
    void shouldDepositMoneySuccessfully() {
        // Arrange
        Account account = new Account("ACC001", "Alice", 100);
        when(accountRepository.findByNumber("ACC001")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Account updated = accountService.deposit("ACC001", 50);

        // Assert
        assertEquals(150, updated.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void shouldRejectZeroDeposit() {
        // Act & Assert
        assertThrows(InvalidAmountException.class, () -> accountService.deposit("ACC001", 0));
    }

    @Test
    void shouldRejectNegativeDeposit() {
        // Act & Assert
        assertThrows(InvalidAmountException.class, () -> accountService.deposit("ACC001", -10));
    }

    // --- Retrait ---

    @Test
    void shouldWithdrawMoneySuccessfully() {
        // Arrange
        Account account = new Account("ACC001", "Alice", 100);
        when(accountRepository.findByNumber("ACC001")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Account updated = accountService.withdraw("ACC001", 30);

        // Assert
        assertEquals(70, updated.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void shouldRejectZeroWithdraw() {
        // Act & Assert
        assertThrows(InvalidAmountException.class, () -> accountService.withdraw("ACC001", 0));
    }

    @Test
    void shouldRejectNegativeWithdraw() {
        // Act & Assert
        assertThrows(InvalidAmountException.class, () -> accountService.withdraw("ACC001", -10));
    }

    @Test
    void shouldRejectWithdrawWithInsufficientFunds() {
        // Arrange
        Account account = new Account("ACC001", "Alice", 10);
        when(accountRepository.findByNumber("ACC001")).thenReturn(Optional.of(account));

        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> accountService.withdraw("ACC001", 50));
    }

    // --- Virement ---

    @Test
    void shouldTransferMoneySuccessfully() {
        // Arrange
        Account fromAccount = new Account("ACC001", "Alice", 100);
        Account toAccount = new Account("ACC002", "Bob", 50);
        when(accountRepository.findByNumber("ACC001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByNumber("ACC002")).thenReturn(Optional.of(toAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        accountService.transfer("ACC001", "ACC002", 30);

        // Assert
        assertEquals(70, fromAccount.getBalance());
        assertEquals(80, toAccount.getBalance());
        verify(accountRepository).save(fromAccount);
        verify(accountRepository).save(toAccount);
    }

    @Test
    void shouldRejectZeroTransfer() {
        // Act & Assert
        assertThrows(InvalidAmountException.class, () -> accountService.transfer("ACC001", "ACC002", 0));
    }

    @Test
    void shouldRejectNegativeTransfer() {
        // Act & Assert
        assertThrows(InvalidAmountException.class, () -> accountService.transfer("ACC001", "ACC002", -10));
    }

    @Test
    void shouldRejectTransferWithInsufficientFunds() {
        // Arrange
        Account fromAccount = new Account("ACC001", "Alice", 10);
        Account toAccount = new Account("ACC002", "Bob", 50);
        when(accountRepository.findByNumber("ACC001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByNumber("ACC002")).thenReturn(Optional.of(toAccount));

        // Act & Assert
        assertThrows(InsufficientFundsException.class,
                () -> accountService.transfer("ACC001", "ACC002", 50));
    }

    @Test
    void shouldRejectTransferToNonExistentAccount() {
        // Arrange
        Account fromAccount = new Account("ACC001", "Alice", 100);
        when(accountRepository.findByNumber("ACC001")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByNumber("ACC999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class,
                () -> accountService.transfer("ACC001", "ACC999", 30));
    }

    @Test
    void shouldRejectTransferFromNonExistentAccount() {
        // Arrange
        when(accountRepository.findByNumber("ACC999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundException.class,
                () -> accountService.transfer("ACC999", "ACC002", 30));
    }
}
