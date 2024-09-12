package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.service.TransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TransferServiceTest {
    @MockBean
    private AccountsRepository accountRepository;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private TransferService transferService;

    @Test
    public void testTransferMoney() {
        // Setup mock accounts with String IDs
        Account accountFrom = new Account("abc123", new BigDecimal("1000"));
        Account accountTo = new Account("def456", new BigDecimal("500"));

        when(accountRepository.getAccount("abc123")).thenReturn(accountFrom);
        when(accountRepository.getAccount("def456")).thenReturn(accountTo);

        // Performing transfer here
        transferService.transferMoney("abc123", "def456", new BigDecimal("200"));

        // Verifying balances
        assertEquals(new BigDecimal("800"), accountFrom.getBalance());
        assertEquals(new BigDecimal("700"), accountTo.getBalance());

        // Verifying that the notifications were sent
        verify(notificationService, times(1))
                .notifyAboutTransfer(accountFrom, "Transferred 200 to account def456");
        verify(notificationService, times(1))
                .notifyAboutTransfer(accountTo, "Received 200 from account abc123");
    }

}
