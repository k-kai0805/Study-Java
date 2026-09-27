package analyst;

import model.Account;
import model.Transaction;
import model.TransactionCriteria;
import model.TypeTransaction;
import model.dto.OutflowSummary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repo.SqlBank;
import repo.SqlAccountBalanceHistoryDao;
import repo.SqlAccountDao;
import repo.SqlTransactionDao;
import repo.AccountBalanceHistoryDao;
import repo.AccountDao;
import repo.SqlAccountDao;
import repo.SqlTransactionDao;
import repo.TransactionDao;
import service.BankService;
import service.IBankService;
import support.MoneyAssert;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cho LOGIC GOM NHOM cua bao cao tong hop.
 *
 * <p>Ky thuat: SummaryReportService in thang ra System.out nen kho test.
 * Day chinh la dau hieu thiet ke - code chi "in" thi kho kiem chung.
 * Nhung AbstractSummaryReport da co san cho moc: no goi render(...).
 * Ta viet mot subclass trong test de BAT du lieu truyen vao, nhung KHONG
 * sua mot dong code production nao.
 *
 * <p>Test quan trong nhat la closingBalance.
 */
class AbstractSummaryReportTest {

    private static final String ACCOUNT_A = "ACC91001";
    private static final String ACCOUNT_B = "ACC91002";
    private static final String ACCOUNT_C = "ACC91003";

    private repo.SqlBank bank;
    private CapturingReport report;
    private repo.AccountDao accountDao;
    private repo.TransactionDao transactionDao;
    private repo.AccountBalanceHistoryDao balanceHistoryDao;
    private service.IBankService bankService;

    @BeforeAll
    static void initDatabase() {
        System.setProperty("config.file", "application-test.properties");
        config.AppConfig.resetForTesting();
        config.DatabaseConnection.initializeTestSchema();
    }

    @BeforeEach
    void setUp() throws Exception {
        repo.SqlAccountDao accountDao = new repo.SqlAccountDao();
        repo.SqlTransactionDao transactionDao = new repo.SqlTransactionDao();
        repo.SqlAccountBalanceHistoryDao balanceHistoryDao = new repo.SqlAccountBalanceHistoryDao();

        // Initialize schema first
        repo.SqlBank bank = new repo.SqlBank(new repo.SqlAccountDao(), new repo.SqlTransactionDao(), new repo.SqlAccountBalanceHistoryDao());

        // Clean database
        try (java.sql.Connection conn = config.DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM account_balance_history");
            stmt.execute("DELETE FROM transaction");
            stmt.execute("DELETE FROM account");
            stmt.execute("DELETE FROM customer");
        }

        // Create a test customer first (required by FK constraint)
        Long customerId;
        try (java.sql.Connection conn = config.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO customer (full_name, cccd, phone) VALUES (?, ?, ?)",
                     java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Test Customer");
            stmt.setString(2, "000000000000");
            stmt.setString(3, "0900000000");
            stmt.executeUpdate();
            try (java.sql.ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    customerId = rs.getLong(1);
                } else {
                    throw new RuntimeException("Failed to create test customer");
                }
            }
        }

        // Create test accounts
        model.Account accountA = accountWith(ACCOUNT_A, "1000000.00",
                trx("TXN-001", ACCOUNT_A, "500000.00", model.TypeTransaction.DEPOSIT, 1, 10),
                trx("TXN-002", ACCOUNT_A, "200000.00", model.TypeTransaction.WITHDRAW, 1, 15),
                trx("TXN-003", ACCOUNT_A, "300000.00", model.TypeTransaction.TRANSFER_OUT, 1, 20),
                trx("TXN-004", ACCOUNT_A, "100000.00", model.TypeTransaction.TRANSFER_IN, 1, 25));
        accountA.setCustomerId(customerId);
        bank.addAccount(accountA);
        // Save transactions for accountA
        for (model.Transaction tx : accountA.getTransactions()) {
            bank.saveTransaction(tx);
        }

        // Tai khoan chi nap, KHONG co tien ra -> phai bi loai khoi bang xep hang
        model.Account accountB = accountWith(ACCOUNT_B, "2000000.00",
                trx("TXN-005", ACCOUNT_B, "1000000.00", model.TypeTransaction.DEPOSIT, 1, 12));
        accountB.setCustomerId(customerId);
        bank.addAccount(accountB);
        // Save transactions for accountB
        for (model.Transaction tx : accountB.getTransactions()) {
            bank.saveTransaction(tx);
        }

        service.BankService bankService = new service.BankService(bank);
        report = new CapturingReport(bank, bankService, balanceHistoryDao);
    }

    @AfterEach
    void tearDown() throws Exception {
        try (java.sql.Connection conn = config.DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM account_balance_history");
            stmt.execute("DELETE FROM transaction");
            stmt.execute("DELETE FROM account");
            stmt.execute("DELETE FROM customer");
        }
    }

    // ------------------------------------------------------------------
    // Chỉ số 1: tổng khối lượng
    // ------------------------------------------------------------------
    @Test
    @DisplayName("chi so 1 = tong moi giao dich trong ky, ke ca dong tien vao")
    void totalVolume_sumsEveryTransaction() {
        report.printSummary(allPeriod());

        // 500.000 + 200.000 + 300.000 + 100.000 + 1.000.000
        support.MoneyAssert.assertMoney("2100000.00", report.totalVolume);
    }

    @Test
    @DisplayName("chi so 1 khong dung isOutflow - tien nap vao van tinh")
    void totalVolume_includesInflows() {
        report.printSummary(criteria(null, java.time.LocalDate.of(2026, 1, 1), java.time.LocalDate.of(2026, 1, 31)));

        // 1.000.000 chi la DEPOT cua ACC91002, van phai nam trong tong khoi luong
        support.MoneyAssert.assertMoney("2100000.00", report.totalVolume);
    }

    @Test
    @DisplayName("ky rong: tong khoi luong bang 0, khong nem loi")
    void emptyPeriod_givesZeroVolume() {
        report.printSummary(criteria(null, java.time.LocalDate.of(2030, 1, 1), java.time.LocalDate.of(2030, 12, 31)));

        support.MoneyAssert.assertMoney("0", report.totalVolume);
        assertTrue(report.outflow.isEmpty());
    }

    // ------------------------------------------------------------------
    // Chỉ số 2: bảng xếp hạng tiền ra
    // ------------------------------------------------------------------
    @Test
    @DisplayName("chi so 2 chi gom dong tien ra, gom theo tai khoan")
    void outflow_containsOnlyOutflows() {
        report.printSummary(allPeriod());

        assertEquals(1, report.outflow.size());

        model.dto.OutflowSummary summary = report.outflow.get(0);
        assertEquals("ACC91001", summary.accountNumber());
        support.MoneyAssert.assertMoney("500000.00", summary.totalOutflow());
    }

    @Test
    @DisplayName("tai khoan khong co tien ra thi KHONG xuat hien")
    void accountWithoutOutflow_isExcluded() {
        report.printSummary(allPeriod());

        // ACC91002 chi co 1 lenh nap. Dua no vao bang xep hang voi gia tri 0
        // se lam bao cao sai - con so 0 khong phai thong tin gi.
        assertNull(findSummary("ACC91002"));
    }

    @Test
    @DisplayName("loc theo loai DEPOSIT thi chi so 2 rong nhung khong loi")
    void filterByDeposit_leavesOutflowEmpty() {
        report.printSummary(criteria(model.TypeTransaction.DEPOSIT, null, null));

        support.MoneyAssert.assertMoney("1500000.00", report.totalVolume);
        assertTrue(report.outflow.isEmpty(),
                "DEPOSIT khong phai tien ra nen chi so 2 phai rong");
    }

    @Test
    @DisplayName("loc ky 20-31/01: chi lay giao dich trong ky do")
    void filterByPeriod_keepsOnlyPeriodTransactions() {
        report.printSummary(criteria(null, java.time.LocalDate.of(2026, 1, 20), java.time.LocalDate.of(2026, 1, 31)));

        support.MoneyAssert.assertMoney("400000.00", report.totalVolume);
        assertEquals(1, report.outflow.size());
        support.MoneyAssert.assertMoney("300000.00", findSummary("ACC91001").totalOutflow());
    }

    // ------------------------------------------------------------------
    // closingBalance - bai hoc #1 trong lessons.md
    // ------------------------------------------------------------------
    @Test
    @DisplayName("closingBalance lay so du cuoi ky, ke ca sau lenh nhan tien")
    void closingBalance_comesFromLastTransactionOfPeriod() {
        report.printSummary(allPeriod());

        // 600.000, KHONG phai 500.000.
        // Giao dich TIEN RA cuoi cung la 20/01 (so du 500.000), nhung
        // sau do 25/01 con mot TRANSFER_IN 100.000. Lay so du cua giao
        // dich tien ra cuoi cung se ra so du TRUNG GIAN.
        support.MoneyAssert.assertMoney("600000.00", findSummary("ACC91001").closingBalance());
    }

    @Test
    @DisplayName("closingAccount khong dung so du hien tai cua tai khoan")
    void closingBalance_isNotCurrentBalance() {
        report.printSummary(allPeriod());

        // So du hien tai trong Bank cung la 600.000 o day, nen test nay
        // chi chay dung khi he thong khong doi so du sau khi in bao cao.
        // Ban chat la BAO CAI PHAI TAI LAI LOP: chay report, them giao
        // dich moi, chay lai - ket qua cua lan truoc phai giu nguyen.
        BigDecimal firstRun = findSummary("ACC91001").closingBalance();

        // Tien nap vao MOT tai khoan KHAC. ACC91002 da ton tai roi,
        // addAccount() se nem BusinessException.
        bank.addAccount(new model.Account("ACC91003", "Tai khoan moi", new java.math.BigDecimal("3000000.00"),
                new ArrayList<>()));
        report.printSummary(allPeriod());

        support.MoneyAssert.assertMoney("600000.00", firstRun, "lan chay truoc phai ton tai");
    }

    // ------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------
    private model.dto.OutflowSummary findSummary(String accountNumber) {
        return report.outflow.stream()
                .filter(s -> s.accountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

    private static model.TransactionCriteria allPeriod() {
        return new model.TransactionCriteria(null, null, null);
    }

    private static model.TransactionCriteria criteria(model.TypeTransaction type, java.time.LocalDate from, java.time.LocalDate to) {
        return new model.TransactionCriteria(type, from, to);
    }

    private static model.Transaction trx(String reference, String account, String amount, model.TypeTransaction type,
                                           int month, int day) {
        return new model.Transaction(reference, account,
                new java.math.BigDecimal(amount),
                type,
                java.time.LocalDateTime.of(2026, month, day, 9, 0),
                new java.math.BigDecimal("0")); // afterBalance
    }

    private static model.Account accountWith(String number, String balance, model.Transaction... transactions) {
        model.Account account = new model.Account(number, "Test " + number, new java.math.BigDecimal(balance), new ArrayList<>());
        for (model.Transaction transaction : transactions) {
            account.addTransaction(transaction);
        }
        return account;
    }

    /**
     * Subclass trong test: bat lai du lieu ma AbstractSummaryReport truyen
     * xuong render(), de khong can in ra man hinh va khong sua code production.
     */
    private final class CapturingReport extends analyst.AbstractSummaryReport {

        private List<model.dto.OutflowSummary> outflow = List.of();
        private java.math.BigDecimal totalVolume = java.math.BigDecimal.ZERO;

        private CapturingReport() {
            super(null, null, null);
        }

        @Override
        protected void render(List<model.dto.OutflowSummary> outflow, java.math.BigDecimal totalVolume) {
            this.outflow = outflow;
            this.totalVolume = totalVolume;
        }
    }
}