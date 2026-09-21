package controller;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Scanner;
import exception.BusinessException;
import model.Account;
import model.TypeTransaction;
import repo.Bank;
import report.TransactionService;
import service.BankService;

@AllArgsConstructor
public class BankingController {
    private final BankService bankService;
    private final Bank bank;
    private final TransactionService transactionService;
    public void handleListAccounts() {
        System.out.println("\n--- LIST ALL ACCOUNTS ---");
        for (Account account : bank.getAllAccounts()) {
            System.out.println(account.getAccountNumber() + " | " + account.getOwnerName()
                    + " | " + account.getBalance());
        }
    }

    public void handleCheckBalance(Scanner scanner) {
        System.out.println("\n--- CHECK BALANCE ---");
        System.out.print("Enter account number: ");
        try {
            String accountNumber = scanner.nextLine();
            Account account = bank.findAccount(accountNumber);
            System.out.println("Owner: " + account.getOwnerName());
            System.out.println("Balance: $" + account.getBalance());
            System.out.println("Recent transactions: " + account.getTransactions());
        } catch (BusinessException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void handleDeposit(Scanner scanner) {
        System.out.println("\n--- DEPOSIT FUNCTION ---");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        try {
            Account account = bank.findAccount(accountNumber);
            System.out.print("Enter amount to deposit: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            bankService.deposit(accountNumber, amount);
            System.out.println("Deposit successful! New balance: $" + account.getBalance());
        } catch (BusinessException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format!");
        }
    }

    public void handlePrintStatement(Scanner scanner) {
        System.out.println("\n--- PRINT STATEMENT ---");
        System.out.print("Enter account number: ");
        try {
            String accountNumber = scanner.nextLine();
            transactionService.printStatement(accountNumber);
        } catch (BusinessException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void handlePrintStatementWithType(Scanner scanner) {
        System.out.println("\n--- PRINT STATEMENT ---");
        try {
            System.out.print("Enter account number: ");
            String accountNumber = scanner.nextLine();
            System.out.print("Enter type of transaction (DEPOSIT / WITHDRAW / TRANSFER_OUT / TRANSFER_IN): ");
            String typeInput = scanner.nextLine();
            TypeTransaction type = TypeTransaction.valueOf(typeInput.toUpperCase());
            transactionService.printStatementWithType(accountNumber, type);
        } catch (BusinessException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void handleWithdraw(Scanner scanner) {
        System.out.println("\n--- WITHDRAW FUNCTION ---");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        try {
            Account account = bank.findAccount(accountNumber);
            System.out.print("Enter amount to withdraw: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            bankService.withdraw(accountNumber, amount);
            System.out.println("Withdraw successful! New balance: $" + account.getBalance());
        } catch (BusinessException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format!");
        }
    }

    public void handleTransfer(Scanner scanner) {
        System.out.println("\n--- TRANSFER FUNCTION ---");
        try {
            System.out.print("Enter FROM account number: ");
            String fromNumber = scanner.nextLine();
            printBalance("From", bank.findAccount(fromNumber));
            System.out.print("Enter TO account number: ");
            String toNumber = scanner.nextLine();
            printBalance("To", bank.findAccount(toNumber));
            System.out.print("Enter amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            bankService.transfer(fromNumber, toNumber, amount);
            System.out.println("Transfer successful!");
        } catch (BusinessException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format!");
        }
    }

    private void printBalance(String label, Account account) {
        System.out.println(label + " - " + account.getAccountNumber() + " - " + account.getOwnerName()
                + ": $" + account.getBalance());
    }
}
