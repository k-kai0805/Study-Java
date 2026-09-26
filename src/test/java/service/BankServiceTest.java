package service;

import exception.AccountNotFoundException;
import exception.BusinessException;
import exception.InsufficientBalanceException;
import exception.InvalidAmountException;
import model.Account;
import model.TypeTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import repo.Bank;
import support.MoneyAssert;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cho QUY TAC NGHIEP VU - noi mot loi lam mat tien that.
 *
 * <p>Khong phai test ham ma, ma test QUY TAC:
 * "tien ra la gi", "rut qua so du bi gi", "chuyen cho chinh minh bi gi".
 * Dung co loi o day la loi nghiep vu, khong phai loi ky thuat.
 */
class BankServiceTest {

    private static final String ACCOUNT_A = "ACC10001";
    private static final String ACCOUNT_B = "ACC10002";

    private Bank bank;
    private BankService service;

    @BeforeEach
    void setUp() {
        // Tao moi moi test: neu dung chung mot instance, test nay lam ban
        // test sau va phai mat thoi gian debug tam thoi.
        bank = new Bank();
        service = new BankService(bank);
    }

    // ------------------------------------------------------------------
    // Hop DONG cua isOutflow
    //
    // Day la test gia tri nhat: chung la "hop dong" cua EnumSet o
    // AbstractBankService. Sau nay them FEE/REVERSED ma quen cap nhat,
    // test se do ro ngay.
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("isOutflow - phan dinh nghia tien ra")
    class IsOutflow {

        @Test
        void withdraw_isOutflow() {
            assertTrue(service.isOutflow(TypeTransaction.WITHDRAW));
        }

        @Test
        void transferOut_isOutflow() {
            assertTrue(service.isOutflow(TypeTransaction.TRANSFER_OUT));
        }

        @Test
        void deposit_isNotOutflow() {
            assertEquals(false, service.isOutflow(TypeTransaction.DEPOSIT));
        }

        @Test
        void transferIn_isNotOutflow() {
            assertEquals(false, service.isOutflow(TypeTransaction.TRANSFER_IN));
        }
    }

    // ------------------------------------------------------------------
    // Deposit
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("deposit")
    class Deposit {

        @Test
        void deposit_positiveAmount_increasesBalance() {
            service.deposit(ACCOUNT_A, new BigDecimal("500000"));

            MoneyAssert.assertMoney("2000000.00", bank.findAccount(ACCOUNT_A).getBalance());
        }

        @Test
        void deposit_zero_throwsInvalidAmount() {
            assertThrows(InvalidAmountException.class,
                    () -> service.deposit(ACCOUNT_A, BigDecimal.ZERO));
        }

        @Test
        void deposit_negative_throwsInvalidAmount() {
            assertThrows(InvalidAmountException.class,
                    () -> service.deposit(ACCOUNT_A, new BigDecimal("-100")));
        }

        @Test
        void deposit_unknownAccount_throwsAccountNotFound() {
            assertThrows(AccountNotFoundException.class,
                    () -> service.deposit("KHONG-CO", new BigDecimal("100")));
        }
    }

    // ------------------------------------------------------------------
    // Withdraw
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("withdraw")
    class Withdraw {

        @Test
        void withdraw_moreThanBalance_throwsInsufficientBalance() {
            // ACC10001 bat dau voi 1.500.000
            assertThrows(InsufficientBalanceException.class,
                    () -> service.withdraw(ACCOUNT_A, new BigDecimal("2000000")));
        }

        @Test
        void withdraw_exactlyBalance_leavesZero() {
            // Bien: rut dung bang so du van hop le, va phai ve dung 0.00
            service.withdraw(ACCOUNT_A, new BigDecimal("1500000.00"));

            MoneyAssert.assertMoney("0.00", bank.findAccount(ACCOUNT_A).getBalance());
        }

        @Test
        void withdraw_oneOverBalance_throwsInsufficientBalance() {
            // Bien: chay 1 dong thoi
            assertThrows(InsufficientBalanceException.class,
                    () -> service.withdraw(ACCOUNT_A, new BigDecimal("1500000.01")));
        }

        @Test
        void withdraw_zero_throwsInvalidAmount() {
            assertThrows(InvalidAmountException.class,
                    () -> service.withdraw(ACCOUNT_A, BigDecimal.ZERO));
        }
    }

    // ------------------------------------------------------------------
    // Transfer
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("transfer")
    class Transfer {

        @Test
        void transfer_toSelf_throwsBusinessException() {
            assertThrows(BusinessException.class,
                    () -> service.transfer(ACCOUNT_A, ACCOUNT_A, new BigDecimal("100")));
        }

        @Test
        void transfer_unknownReceiver_throwsAccountNotFound() {
            assertThrows(AccountNotFoundException.class,
                    () -> service.transfer(ACCOUNT_A, "KHONG-CO", new BigDecimal("100")));
        }

        @Test
        void transfer_moreThanBalance_throwsInsufficientBalance() {
            assertThrows(InsufficientBalanceException.class,
                    () -> service.transfer(ACCOUNT_A, ACCOUNT_B, new BigDecimal("2000000")));
        }

        @Test
        void transfer_movesMoneyOnBothSides() {
            service.transfer(ACCOUNT_A, ACCOUNT_B, new BigDecimal("300000"));

            MoneyAssert.assertMoney("1200000.00", bank.findAccount(ACCOUNT_A).getBalance());
            MoneyAssert.assertMoney("1100000.00", bank.findAccount(ACCOUNT_B).getBalance());
        }

        @Test
        void transfer_createsTwoLedgerEntries() {
            // Bien: 1 lenh chuyen = 2 dong so cai (ra + vao).
            // Day la ly do "tong khoi luong" dem 2 lan mot lenh chuyen.
            service.transfer(ACCOUNT_A, ACCOUNT_B, new BigDecimal("300000"));

            assertEquals(1, bank.findAccount(ACCOUNT_A).getTransactions().size());
            assertEquals(1, bank.findAccount(ACCOUNT_B).getTransactions().size());
        }
    }

    // ------------------------------------------------------------------
    // BigDecimal - chung minh bang test, khong chi noi
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("BigDecimal - chung minh vi sao khong dung double")
    class BigDecimalPrecision {

        @Test
        void repeatedSmallAmounts_stayExact() {
            // Neu dung double: 0.1 + 0.2 = 0.30000000000000004
            // Day la cau hoi phong van kinh dien, va test la bang chung.
            service.deposit(ACCOUNT_A, new BigDecimal("0.1"));
            service.deposit(ACCOUNT_A, new BigDecimal("0.2"));

            MoneyAssert.assertMoney("1500000.30", bank.findAccount(ACCOUNT_A).getBalance());
        }

        @Test
        void manySmallDeposits_stayExact() {
            // 100 lan nap 0.01 = 1.00. ACC10001 bat dau 1.500.000
            // -> 1.500.001. Dung double se lech o lan lap thu hai tro len.
            for (int i = 0; i < 100; i++) {
                service.deposit(ACCOUNT_A, new BigDecimal("0.01"));
            }

            MoneyAssert.assertMoney("1500001.00", bank.findAccount(ACCOUNT_A).getBalance());
        }
    }

    // ------------------------------------------------------------------
    // Sổ chỉ đọc
    // ------------------------------------------------------------------
    @Test
    @DisplayName("getTransactions() tra ve list chi-doc")
    void transactionList_isUnmodifiable() {
        service.deposit(ACCOUNT_A, new BigDecimal("1000"));

        Account account = bank.findAccount(ACCOUNT_A);

        assertThrows(UnsupportedOperationException.class,
                () -> account.getTransactions().clear());
    }
}
