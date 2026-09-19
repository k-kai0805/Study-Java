import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Account currentAccount = new Account("Quoc Khanh", new BigDecimal("1000"), new ArrayList<>());
        BankService bankService = new BankService();
        BankingController atmController = new BankingController(bankService);

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        while (choice != 4) {
            System.out.println("\n===========================");
            System.out.println("      ABC BANKING MENU     ");
            System.out.println("===========================");
            System.out.println("1. Check balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Exit");
            System.out.print("Please choose a function: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        atmController.handleCheckBalance(currentAccount);
                        break;
                    case 2:
                        atmController.handleDeposit(currentAccount, scanner);
                        break;
                    case 3:
                        atmController.handleWithdraw(currentAccount, scanner);
                        break;
                    case 4:
                        System.out.println("Thank you for using ABC Banking!");
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            } else {
                System.out.println("Please enter a valid number!");
                scanner.next(); // Clear invalid input
            }
        }
        scanner.close();
    }
}