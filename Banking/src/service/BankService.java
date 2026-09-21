package service;

import model.Account;
import model.Transaction;
import model.TypeTransaction;
import repo.Bank;
import utils.MoneyUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankService extends AbstractBankService {

    public BankService(Bank bank) {
        super(bank);
    }

    @Override
    protected void doDeposit(Account account, BigDecimal amount) {
        BigDecimal newBalance = MoneyUtil.round(account.getBalance().add(amount));
        account.setBalance(newBalance);
        account.addTransaction(new Transaction(amount, TypeTransaction.DEPOSIT, LocalDateTime.now(), newBalance));
    }

    @Override
    protected void doWithdraw(Account account, BigDecimal amount) {
        BigDecimal newBalance = MoneyUtil.round(account.getBalance().subtract(amount));
        account.setBalance(newBalance);
        account.addTransaction(new Transaction(amount, TypeTransaction.WITHDRAW, LocalDateTime.now(), newBalance));
    }

    @Override
    protected void doTransfer(Account from, Account to, BigDecimal amount) {
        BigDecimal fromBalance = MoneyUtil.round(from.getBalance().subtract(amount));
        from.setBalance(fromBalance);
        from.addTransaction(new Transaction(amount, TypeTransaction.TRANSFER_OUT, LocalDateTime.now(), fromBalance));
        BigDecimal toBalance = MoneyUtil.round(to.getBalance().add(amount));
        to.setBalance(toBalance);
        to.addTransaction(new Transaction(amount, TypeTransaction.TRANSFER_IN, LocalDateTime.now(), toBalance));
    }
}
