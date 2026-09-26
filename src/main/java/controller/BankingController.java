package controller;

import analyst.SummaryReportService;
import countTransaction.StatisticsReportService;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import exception.BusinessException;
import model.Account;
import model.ReportRequest;
import model.TransactionCriteria;
import model.TypeTransaction;
import repo.Bank;
import report.TransactionReportService;
import service.BankService;
import service.IBankService;

@AllArgsConstructor
public class BankingController {
    private final IBankService IbankService;
    private final Bank bank;
    private final TransactionReportService transactionReportService;
    private final StatisticsReportService statisticsReportService;
    private final SummaryReportService summaryReportService;
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
            IbankService.deposit(accountNumber, amount);
            System.out.println("Deposit successful! New balance: $" + account.getBalance());
        } catch (BusinessException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format!");
        }
    }

    public void handlePrintStatement(Scanner scanner) {

        System.out.println("\n--- PRINT STATEMENT ---");

        try {
            ReportRequest request =
                    readReportRequest(scanner);

            transactionReportService.printStatement(
                    request.accountNumber(),
                    request.criteria()
            );

        } catch (BusinessException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void handlePrintCountTransaction(Scanner scanner) {

        System.out.println("\n--- PRINT TRANSACTION FUNCTION ---");

        try {
            ReportRequest request =
                    readReportRequest(scanner);

            statisticsReportService.countTransaction(
                    request.accountNumber(),
                    request.criteria()
            );

        } catch (BusinessException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void handlePrintSummary(Scanner scanner){
        System.out.println("\n--- PRINT SUMMARY FUNCTION ---");

        try {
            TransactionCriteria criteria =
                    readCriteria(scanner);
            summaryReportService.printSummary(
                    criteria
            );
        } catch (BusinessException e) {
            System.out.println(
                    "Error: " + e.getMessage()
            );
        }


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

    private ReportRequest readReportRequest(
            Scanner scanner) {

        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        return new ReportRequest(
                accountNumber,
                readCriteria(scanner)
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
            Account account = bank.findAccount(accountNumber);
            System.out.print("Enter amount to withdraw: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            IbankService.withdraw(accountNumber, amount);
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
            IbankService.transfer(fromNumber, toNumber, amount);
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
