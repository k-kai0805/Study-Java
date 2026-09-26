package repo;

import exception.AccountNotFoundException;
import model.Account;
import model.Transaction;
import model.TransactionCriteria;
import model.TypeTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.MoneyAssert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cho DAO.
 *
 * <p>Diem quan trong: khong the them giao dich vao tai khoan co that duoc
 * (Account.addTransaction la public) nen tao du lieu o NGAY CO DINH trong
 * qua khu. Neu dung lenh that ta moi co the kiem tra loc theo ngay - con
 * dung LocalDateTime.now() thi moi giao dich deu la "hom nay" va test loc
 * ngay khong co y nghia gi.
 *
 * <p>Test o day phu them ca private helper matchesCriteria() - ham chung
 * cho findTransactions() va findAllTransactions().
 */
class BankTest {

    private static final String ACCOUNT_A = "ACC90001";
    private static final String ACCOUNT_B = "ACC90002";

    private Bank bank;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        bank.addAccount(accountWith(ACCOUNT_A, "1000000.00",
                trx(ACCOUNT_A, "500000.00", TypeTransaction.DEPOSIT, 1, 10, "1000000.00"),
                trx(ACCOUNT_A, "200000.00", TypeTransaction.WITHDRAW, 1, 20, "800000.00"),
                trx(ACCOUNT_A, "100000.00", TypeTransaction.TRANSFER_IN, 2, 5, "900000.00")));
        bank.addAccount(accountWith(ACCOUNT_B, "800000.00",
                trx(ACCOUNT_B, "300000.00", TypeTransaction.DEPOSIT, 1, 15, "800000.00"),
                trx(ACCOUNT_B, "50000.00", TypeTransaction.DEPOSIT, 2, 10, "850000.00")));
    }

    // ------------------------------------------------------------------
    // Loc theo criteria
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("matchesCriteria - loc dung theo ca 3 truong")
    class Filtering {

        @Test
        @DisplayName("ca 3 truong null = khong loc, tra ve tat ca")
        void allNull_returnsEverything() {
            List<Transaction> result = bank.findAllTransactions(criteria(null, null, null));

            assertEquals(5, result.size());
        }

        @Test
        @DisplayName("loc theo loai giao dich")
        void byType_filtersCorrectly() {
            List<Transaction> result = bank.findAllTransactions(
                    criteria(TypeTransaction.DEPOSIT, null, null));

            assertEquals(3, result.size());
            assertTrue(result.stream().allMatch(t -> t.type() == TypeTransaction.DEPOSIT));
        }

        @Test
        @DisplayName("loc theo from, tra ve ca muc do bang from")
        void byFromDate_isInclusive() {
            // Giao dich tren hoac sau 20/01:
            // ACC90001 20/01, 05/02 · ACC90002 10/02  -> 3
            List<Transaction> result = bank.findAllTransactions(
                    criteria(null, LocalDate.of(2026, 1, 20), null));

            assertEquals(3, result.size());
        }

        @Test
        @DisplayName("loc theo to, tra ve ca muc do bang to")
        void byToDate_isInclusive() {
            List<Transaction> result = bank.findAllTransactions(
                    criteria(null, null, LocalDate.of(2026, 1, 31)));

            assertEquals(3, result.size());
        }

        @Test
        @DisplayName("loc ca khoang: tu 15/01 den 31/01")
        void byDateRange_filtersCorrectly() {
            List<Transaction> result = bank.findAllTransactions(
                    criteria(null, LocalDate.of(2026, 1, 15), LocalDate.of(2026, 1, 31)));

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("from > to thi khong co giao dich nao thoa man")
        void fromAfterTo_returnsEmpty() {
            // Bank KHONG chan truoc - no tra ve rong. Chan truoc
            // (from > to) thuoc ve tang Controller.
            List<Transaction> result = bank.findAllTransactions(
                    criteria(null, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 1, 1)));

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("khoang khong co du lieu thi tra ve rong, khong nem loi")
        void periodWithNoData_returnsEmptyNotError() {
            List<Transaction> result = bank.findAllTransactions(
                    criteria(null, LocalDate.of(2030, 1, 1), LocalDate.of(2030, 12, 31)));

            assertTrue(result.isEmpty());
        }
    }

    // ------------------------------------------------------------------
    // findAllTransactions vs findTransactions
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Pham vi tra ve")
    class Scope {

        @Test
        @DisplayName("findAllTransactions tra ve ca he thong")
        void findAll_coversEveryAccount() {
            List<Transaction> result = bank.findAllTransactions(criteria(null, null, null));

            assertTrue(result.stream().anyMatch(t -> t.accountNumber().equals(ACCOUNT_A)));
            assertTrue(result.stream().anyMatch(t -> t.accountNumber().equals(ACCOUNT_B)));
        }

        @Test
        @DisplayName("findTransactions chi tra ve 1 tai khoan")
        void findByAccount_staysWithinThatAccount() {
            List<Transaction> result =
                    bank.findTransactions(ACCOUNT_A, criteria(null, null, null));

            assertEquals(3, result.size());
            assertTrue(result.stream().allMatch(t -> t.accountNumber().equals(ACCOUNT_A)));
        }

        @Test
        @DisplayName("hai method dung cung mot bo loc")
        void bothMethods_shareTheSameFilter() {
            // Kiem tra tinh nhat quan: cung 1 criteria, ket qua phai khop
            // voi phan con lai cua tong so giao dich.
            TransactionCriteria c = criteria(TypeTransaction.DEPOSIT, null, null);

            long fromAll = bank.findAllTransactions(c).size();
            long fromA = bank.findTransactions(ACCOUNT_A, c).size();
            long fromB = bank.findTransactions(ACCOUNT_B, c).size();

            assertEquals(fromAll, fromA + fromB);
        }
    }

    // ------------------------------------------------------------------
    // Tra cu tai khoan
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("findAccount")
    class FindAccount {

        @Test
        void existingAccount_returnsAccount() {
            assertEquals(ACCOUNT_A, bank.findAccount(ACCOUNT_A).getAccountNumber());
        }

        @Test
        void unknownAccount_throwsAccountNotFound() {
            assertThrows(AccountNotFoundException.class, () -> bank.findAccount("KHONG-CO"));
        }

        @Test
        @DisplayName("tim tai khoan khong ton tai thi nem loi TRUOC khi loc")
        void findTransactions_unknownAccount_throwsBeforeFiltering() {
            assertThrows(AccountNotFoundException.class,
                    () -> bank.findTransactions("KHONG-CO", criteria(null, null, null)));
        }

        @Test
        void duplicateAccountNumber_isRejected() {
            assertThrows(RuntimeException.class,
                    () -> bank.addAccount(accountWith(ACCOUNT_A, "0.00")));
        }
    }

    // ------------------------------------------------------------------
    // Tinh toan tren du lieu loc duoc
    // ------------------------------------------------------------------
    @Test
    @DisplayName("tong tien cua cac giao dich loc duoc dung chinh xac")
    void sumOfFilteredTransactions_isExact() {
        List<Transaction> result = bank.findAllTransactions(
                criteria(null, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)));

        BigDecimal total = result.stream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        MoneyAssert.assertMoney("1000000.00", total);
    }

    // ------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------
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
}
