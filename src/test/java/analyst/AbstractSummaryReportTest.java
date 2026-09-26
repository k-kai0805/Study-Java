package analyst;

import model.Account;
import model.Transaction;
import model.TransactionCriteria;
import model.TypeTransaction;
import model.dto.OutflowSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repo.Bank;
import service.BankService;
import service.IBankService;
import support.MoneyAssert;

import java.math.BigDecimal;
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

    private Bank bank;
    private CapturingReport report;

    @BeforeEach
    void setUp() {
        bank = new Bank();

        // Giao dich cuoi cung trong ky la TRANSFER_IN - day la cai bay
        // quan trong nhat. So du cuoi ky phai la 600.000 (sau lenh nhan
        // vao), KHONG phai 500.000 (so du ngay truoc lenh nhan vao).
        bank.addAccount(accountWith(ACCOUNT_A, "1000000.00",
                trx(ACCOUNT_A, "500000.00", TypeTransaction.DEPOSIT, 1, 10, "1000000.00"),
                trx(ACCOUNT_A, "200000.00", TypeTransaction.WITHDRAW, 1, 15, "800000.00"),
                trx(ACCOUNT_A, "300000.00", TypeTransaction.TRANSFER_OUT, 1, 20, "500000.00"),
                trx(ACCOUNT_A, "100000.00", TypeTransaction.TRANSFER_IN, 1, 25, "600000.00")));

        // Tai khoan chi nap, KHONG co tien ra -> phai bi loai khoi bang xep hang
        bank.addAccount(accountWith(ACCOUNT_B, "2000000.00",
                trx(ACCOUNT_B, "1000000.00", TypeTransaction.DEPOSIT, 1, 12, "2000000.00")));

        IBankService bankService = new BankService(bank);
        report = new CapturingReport(bank, bankService);
    }

    // ------------------------------------------------------------------
    // Chỉ số 1: tổng khối lượng
    // ------------------------------------------------------------------
    @Test
    @DisplayName("chi so 1 = tong moi giao dich trong ky, ke ca dong tien vao")
    void totalVolume_sumsEveryTransaction() {
        report.printSummary(allPeriod());

        // 500.000 + 200.000 + 300.000 + 100.000 + 1.000.000
        MoneyAssert.assertMoney("2100000.00", report.totalVolume);
    }

    @Test
    @DisplayName("chi so 1 khong dung isOutflow - tien nap vao van tinh")
    void totalVolume_includesInflows() {
        report.printSummary(criteria(null, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)));

        // 1.000.000 chi la DEPOT cua ACC91002, van phai nam trong tong khoi luong
        MoneyAssert.assertMoney("2100000.00", report.totalVolume);
    }

    @Test
    @DisplayName("ky rong: tong khoi luong bang 0, khong nem loi")
    void emptyPeriod_givesZeroVolume() {
        report.printSummary(criteria(null, LocalDate.of(2030, 1, 1), LocalDate.of(2030, 12, 31)));

        MoneyAssert.assertMoney("0", report.totalVolume);
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

        OutflowSummary summary = report.outflow.get(0);
        assertEquals(ACCOUNT_A, summary.accountNumber());
        MoneyAssert.assertMoney("500000.00", summary.totalOutflow());
    }

    @Test
    @DisplayName("tai khoan khong co tien ra thi KHONG xuat hien")
    void accountWithoutOutflow_isExcluded() {
        report.printSummary(allPeriod());

        // ACC91002 chi co 1 lenh nap. Dua no vao bang xep hang voi gia tri 0
        // se lam bao cao sai - con so 0 khong phai thong tin gi.
        assertNull(findSummary(ACCOUNT_B));
    }

    @Test
    @DisplayName("loc theo loai DEPOSIT thi chi so 2 rong nhung khong loi")
    void filterByDeposit_leavesOutflowEmpty() {
        report.printSummary(criteria(TypeTransaction.DEPOSIT, null, null));

        MoneyAssert.assertMoney("1500000.00", report.totalVolume);
        assertTrue(report.outflow.isEmpty(),
                "DEPOSIT khong phai tien ra nen chi so 2 phai rong");
    }

    @Test
    @DisplayName("loc ky 20-31/01: chi lay giao dich trong ky do")
    void filterByPeriod_keepsOnlyPeriodTransactions() {
        report.printSummary(criteria(null, LocalDate.of(2026, 1, 20), LocalDate.of(2026, 1, 31)));

        MoneyAssert.assertMoney("400000.00", report.totalVolume);
        assertEquals(1, report.outflow.size());
        MoneyAssert.assertMoney("300000.00", findSummary(ACCOUNT_A).totalOutflow());
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
        MoneyAssert.assertMoney("600000.00", findSummary(ACCOUNT_A).closingBalance());
    }

    @Test
    @DisplayName("closingAccount khong dung so du hien tai cua tai khoan")
    void closingBalance_isNotCurrentBalance() {
        report.printSummary(allPeriod());

        // So du hien tai trong Bank cung la 600.000 o day, nen test nay
        // chi chay dung khi he thong khong doi so du sau khi in bao cao.
        // Ban chat la BAO CAI PHAI TAI LAI LOP: chay report, them giao
        // dich moi, chay lai - ket qua cua lan truoc phai giu nguyen.
        BigDecimal firstRun = findSummary(ACCOUNT_A).closingBalance();

        // Tien nap vao MOT tai khoan KHAC. ACC91002 da ton tai roi,
        // addAccount() se nem BusinessException.
        bank.addAccount(new Account(ACCOUNT_C, "Tai khoan moi", new BigDecimal("3000000.00"),
                new ArrayList<>()));
        report.printSummary(allPeriod());

        MoneyAssert.assertMoney("600000.00", firstRun, "lan chay truoc phai ton tai");
    }

    // ------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------
    private OutflowSummary findSummary(String accountNumber) {
        return report.outflow.stream()
                .filter(s -> s.accountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

    private static TransactionCriteria allPeriod() {
        return new TransactionCriteria(null, null, null);
    }

    private static TransactionCriteria criteria(TypeTransaction type, LocalDate from, LocalDate to) {
        return new TransactionCriteria(type, from, to);
    }

    private static Transaction trx(String account, String amount, TypeTransaction type,
                                   int month, int day, String afterBalance) {
        return new Transaction(account,
                new BigDecimal(amount),
                type,
                LocalDateTime.of(2026, month, day, 9, 0),
                new BigDecimal(afterBalance));
    }

    private static Account accountWith(String number, String balance, Transaction... transactions) {
        Account account = new Account(number, "Test " + number, new BigDecimal(balance), new ArrayList<>());
        for (Transaction transaction : transactions) {
            account.addTransaction(transaction);
        }
        return account;
    }

    /**
     * Subclass trong test: bat lai du lieu ma AbstractSummaryReport truyen
     * xuong render(), de khong can in ra man hinh va khong sua code production.
     */
    private static final class CapturingReport extends AbstractSummaryReport {

        private List<OutflowSummary> outflow = List.of();
        private BigDecimal totalVolume = BigDecimal.ZERO;

        private CapturingReport(Bank bank, IBankService bankService) {
            super(bank, bankService);
        }

        @Override
        protected void render(List<OutflowSummary> outflow, BigDecimal totalVolume) {
            assertNotNull(outflow);
            this.outflow = outflow;
            this.totalVolume = totalVolume;
        }
    }
}
