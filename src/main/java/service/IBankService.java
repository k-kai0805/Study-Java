package service;

import model.TypeTransaction;

import java.math.BigDecimal;

public interface IBankService {
    void deposit(String accountNumber, BigDecimal amount);
    void withdraw(String accountNumber, BigDecimal amount);
    void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount);
    boolean isOutflow(TypeTransaction typeTransaction);
    BigDecimal checkAccount(String accountNumber);
}