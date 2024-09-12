package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InvalidRequestException;
import com.dws.challenge.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransferService {
    private final AccountsRepository accountRepository;
    private final NotificationService notificationService;

    //Constructor injection
    @Autowired
    public TransferService(AccountsRepository accountRepository, NotificationService notificationService) {
        this.accountRepository = accountRepository;
        this.notificationService = notificationService;
    }

    /**
     * This method is used to transfer money between the accounts.
     * @param accountFromId From Account id
     * @param accountToId To account id
     * @param amount The amount to transfer
     */
    public void transferMoney(String accountFromId, String accountToId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Account accountFrom = accountRepository.getAccount(accountFromId);
        if(accountFrom==null){
            throw new InvalidRequestException("accountFrom is not valid");
        }
        Account accountTo = accountRepository.getAccount(accountToId);
        if(accountTo==null){
            throw new InvalidRequestException("accountTo is not valid");
        }

        // Synchronize by locking account IDs to avoid deadlock while ensuring consistent ordering
        Object lock1 = accountFromId.compareTo(accountToId)<0 ? accountFrom : accountTo;
        Object lock2 = accountFromId.compareTo(accountToId)<0 ? accountTo : accountFrom;

        synchronized (lock1) {
            synchronized (lock2) {
                if (accountFrom.getBalance().compareTo(amount) < 0) {
                    throw new InvalidRequestException("Insufficient balance in account: " + accountFromId);
                }

                accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
                accountTo.setBalance(accountTo.getBalance().add(amount));

                // Saving changes to both accounts
                accountRepository.updateAccount(accountFrom);
                accountRepository.updateAccount(accountTo);
            }
        }


        //Notify the account holders about the transfer
        notificationService.notifyAboutTransfer(accountFrom, "Transferred " + amount + " to account " + accountToId);
        notificationService.notifyAboutTransfer(accountTo, "Received " + amount + " from account " + accountFromId);
    }
}
