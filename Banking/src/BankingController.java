import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Scanner;
import exception.BusinessException;

@AllArgsConstructor
public class BankingController {
    private final BankService bankService;
    public void handleDeposit(Account account, Scanner scanner) {
        System.out.println("\n--- DEPOSIT FUNCTION ---");
        System.out.print("Please enter amount to deposit: ");

        try {
            BigDecimal amount = scanner.nextBigDecimal();
            bankService.deposit(account, amount);
            System.out.println("Deposit successful! New balance: $" + account.getBalance());
            System.out.println("Transaction time: " + account.getTransactions());
        } catch (BusinessException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input format!");
            scanner.nextLine(); // Clear buffer
        }
    }

    public void handleWithdraw(Account account, Scanner scanner) {
        System.out.println("\n--- WITHDRAW FUNCTION ---");
        System.out.print("Please enter amount to withdraw: ");

        try {
            BigDecimal amount = scanner.nextBigDecimal();
            bankService.withdraw(account, amount);
            System.out.println("Withdraw successful! New balance: $" + account.getBalance());
            System.out.println("Transaction time: " + account.getTransactions());
        } catch (BusinessException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input format!");
            scanner.nextLine();
        }
    }

    public void handleCheckBalance(Account account) {
        System.out.println("\n--- CHECK BALANCE ---");
        BigDecimal balance = bankService.checkAccount(account);
        System.out.println("Current balance for " + account.getBankName() + ": $" + balance);
        System.out.println("Transaction time: " + account.getTransactions());
    }
}
