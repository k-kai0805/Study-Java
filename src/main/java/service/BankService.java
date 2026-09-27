package service;

import exception.*;
import model.Account;
import model.Transaction;
import model.TypeTransaction;
import repo.SqlBank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankService implements IBankService {

    private final SqlBank bank;

    public BankService(SqlBank bank) {
        this.bank = bank;
    }

    @Override
    public void deposit(String accountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }
        bank.deposit(accountNumber, amount);
    }

    @Override
    public void withdraw(String accountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Withdraw amount must be greater than zero");
        }
        bank.withdraw(accountNumber, amount);
    }

    @Override
    public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        if (fromAccountNumber == null || toAccountNumber == null || fromAccountNumber.equals(toAccountNumber)) {
            throw new BusinessException("Cannot transfer to yourself or invalid account numbers");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transfer amount must be greater than zero");
        }
        bank.transfer(fromAccountNumber, toAccountNumber, amount);
    }

    @Override
    public BigDecimal checkAccount(String accountNumber) {
        return bank.findAccountOrThrow(accountNumber).getBalance();
    }

    @Override
    public boolean isOutflow(model.TypeTransaction type) {
        return type == model.TypeTransaction.WITHDRAW || type == model.TypeTransaction.TRANSFER_OUT;
    }

    // Expose bank for report services
    public SqlBank getBank() {
        return bank;
    }
}