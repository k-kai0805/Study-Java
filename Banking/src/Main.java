import countTransaction.StatisticsReportService;
import controller.BankingController;
import repo.Bank;
import report.TransactionReportService;
import service.BankService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        BankService bankService = new BankService(bank);
        TransactionReportService transactionReportService = new TransactionReportService(bank);
        StatisticsReportService statisticsReportService = new StatisticsReportService(bank);
        BankingController controller = new BankingController(bankService, bank, transactionReportService, statisticsReportService);

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        while (choice != 8) {
            System.out.println("\n===========================");
            System.out.println("      ABC BANKING MENU     ");
            System.out.println("===========================");
            System.out.println("1. Check balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. List all accounts");
            System.out.println("6. Print Statement");
            System.out.println("7. Print Transaction");
            System.out.println("8. Exit");
            System.out.print("Please choose a function: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();                      // nuốt dòng trống sau số
                switch (choice) {
                    case 1 -> controller.handleCheckBalance(scanner);
                    case 2 -> controller.handleDeposit(scanner);
                    case 3 -> controller.handleWithdraw(scanner);
                    case 4 -> controller.handleTransfer(scanner);
                    case 5 -> controller.handleListAccounts();
                    case 6 -> controller.handlePrintStatement(scanner);
                    case 7 -> controller.printCountTransaction(scanner);
                    case 8 -> System.out.println("Thank you for using ABC Banking!");
                    default -> System.out.println("Invalid choice! Please try again.");
                }
            } else {
                System.out.println("Please enter a valid number!");
                scanner.next();
            }
        }
        scanner.close();
    }
}