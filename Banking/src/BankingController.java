import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Scanner;

@AllArgsConstructor
public class BankingController {
    private final BankService bankService;
    public void handleDeposit(Accounts accounts, Scanner scanner) {
        System.out.println("\n--- DEPOSIT FUNCTION ---");
        System.out.print("Please enter amount to deposit: ");

        try {
            BigDecimal amount = scanner.nextBigDecimal();
            bankService.deposit(accounts, amount);
            System.out.println("Deposit successful! New balance: $" + accounts.getAmount());
            System.out.println("Total transactions made: " + accounts.getTransactionCount());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input format!");
            scanner.nextLine(); // Clear buffer
        }
    }

    public void handleWithdraw(Accounts accounts, Scanner scanner) {
        System.out.println("\n--- WITHDRAW FUNCTION ---");
        System.out.print("Please enter amount to withdraw: ");

        try {
            BigDecimal amount = scanner.nextBigDecimal();
            bankService.withdraw(accounts, amount);
            System.out.println("Withdraw successful! New balance: $" + accounts.getAmount());
            System.out.println("Total transactions made: " + accounts.getTransactionCount());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input format!");
            scanner.nextLine();
        }
    }

    public void handleCheckBalance(Accounts accounts) {
        System.out.println("\n--- CHECK BALANCE ---");
        BigDecimal balance = bankService.checkAccount(accounts);
        System.out.println("Current balance for " + accounts.getBankName() + ": $" + balance);
        System.out.println("Total transactions made: " + accounts.getTransactionCount());
    }
}
