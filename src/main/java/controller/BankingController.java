package controller;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;
import exception.BusinessException;
import model.Account;
import model.ReportRequest;
import model.TransactionCriteria;
import model.TypeTransaction;
import repo.SqlBank;
import service.BankService;
import service.IBankService;

@AllArgsConstructor
public class BankingController {
    private final IBankService bankService;
    private final SqlBank bank;

    public void handleListAccounts() {
        System.out.println("\n--- LIST ALL ACCOUNTS ---");
        for (Account account : bank.getAllAccounts()) {
            System.out.println(account.getAccountNumber() + " | " + account.getFullName()
                    + " | " + account.getBalance());
        }
    }

    public void handleCheckBalance(Scanner scanner) {
        System.out.println("\n--- CHECK BALANCE ---");
        System.out.print("Enter account number: ");
        try {
            String accountNumber = scanner.nextLine();
            Optional<Account> accountOpt = bank.findAccount(accountNumber);
            if (accountOpt.isEmpty()) {
                throw new BusinessException("ACCOUNT_NOT_FOUND: " + accountNumber);
            }
            Account account = accountOpt.get();
            System.out.println("Fullname: " + account.getFullName());
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
            Optional<Account> accountOpt = bank.findAccount(accountNumber);
            if (accountOpt.isEmpty()) {
                throw new BusinessException("ACCOUNT_NOT_FOUND: " + accountNumber);
            }
            System.out.print("Enter amount to deposit: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            bankService.deposit(accountNumber, amount);
            System.out.println("Deposit successful! New balance: $" + bank.findAccount(accountNumber).orElseThrow().getBalance());
        } catch (BusinessException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format!");
        }
    }

    public void handlePrintStatement(Scanner scanner) {
        System.out.println("\n--- PRINT STATEMENT ---");
        System.out.println("Not implemented yet");
    }

    public void handlePrintCountTransaction(Scanner scanner) {
        System.out.println("\n--- PRINT TRANSACTION FUNCTION ---");
        System.out.println("Not implemented yet");
    }

    public void handlePrintSummary(Scanner scanner){
        System.out.println("\n--- PRINT SUMMARY FUNCTION ---");
        System.out.println("Not implemented yet");
    }

    private TransactionCriteria readCriteria(Scanner scanner) {
        System.out.print(
                "Transaction type (DEPOSIT/WITHDRAW/TRANSFER_OUT/TRANSFER_IN, blank for all): "
        );
        String typeInput = scanner.nextLine();

        TypeTransaction type = parseType(typeInput);

        System.out.print("From date (yyyy-MM-dd, blank for all): ");
        LocalDate fromDate = parseDate(scanner.nextLine());

        System.out.print("To date (yyyy-MM-dd, blank for all): ");
        LocalDate toDate = parseDate(scanner.nextLine());

        if (fromDate != null
                && toDate != null
                && fromDate.isAfter(toDate)) {

            throw new BusinessException(
                    "'From date' must be earlier than or equal to 'To date'."
            );
        }

        return new TransactionCriteria(
                type,
                fromDate,
                toDate
        );
    }

    private LocalDate parseDate(String input) {
        if (input.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new BusinessException(
                    "Invalid date format. Use yyyy-MM-dd."
            );
        }
    }

    private TypeTransaction parseType(String input) {
        if (input.isBlank()) {
            return null;
        }
        try {
            return TypeTransaction.fromString(input);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "Invalid transaction type. Valid values: DEPOSIT, WITHDRAW, TRANSFER_OUT, TRANSFER_IN"
            );
        }
    }

    public void handleWithdraw(Scanner scanner) {
        System.out.println("\n--- WITHDRAW FUNCTION ---");
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        try {
            Optional<Account> accountOpt = bank.findAccount(accountNumber);
            if (accountOpt.isEmpty()) {
                throw new BusinessException("ACCOUNT_NOT_FOUND: " + accountNumber);
            }
            System.out.print("Enter amount to withdraw: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            bankService.withdraw(accountNumber, amount);
            System.out.println("Withdraw successful! New balance: $" + bank.findAccount(accountNumber).orElseThrow().getBalance());
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
            printBalance("From", bank.findAccount(fromNumber).orElseThrow());
            System.out.print("Enter TO account number: ");
            String toNumber = scanner.nextLine();
            printBalance("To", bank.findAccount(toNumber).orElseThrow());
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
        System.out.println(label + " - " + account.getAccountNumber() + " - "
                + ": $" + account.getBalance());
    }
}