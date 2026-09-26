package utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.MoneyAssert;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test cho MoneyUtil - ham thuan, chay nhanh, xay niem tin vao phep tinh tien.
 *
 * <p>Hai ham nay duoc dung o MOI noi in tien. Mot test sai o day se lam
 * sai o moi bao cao.
 */
class MoneyUtilTest {

    // ------------------------------------------------------------------
    // round
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("round - lam tron HALF_UP ve 2 chu so thap phan")
    class Round {

        @Test
        void moreThanTwoDecimals_roundsDown() {
            MoneyAssert.assertMoney("1234.56", MoneyUtil.round(new BigDecimal("1234.564")));
        }

        @Test
        void moreThanTwoDecimals_roundsUp() {
            MoneyAssert.assertMoney("1234.57", MoneyUtil.round(new BigDecimal("1234.567")));
        }

        @Test
        void exactlyHalf_roundsUp_HALF_UP() {
            // HALF_UP: 0.005 -> 0.01. Neu dung HALF_EVEN se ra 0.00
            // -> quy tac khac nhau, tien se lech.
            MoneyAssert.assertMoney("0.01", MoneyUtil.round(new BigDecimal("0.005")));
        }

        @Test
        void alreadyTwoDecimals_unchanged() {
            MoneyAssert.assertMoney("500.00", MoneyUtil.round(new BigDecimal("500.00")));
        }

        @Test
        void integerValue_getsScaleTwo() {
            MoneyAssert.assertMoney("500.00", MoneyUtil.round(new BigDecimal("500")));
        }

        @Test
        void null_throwsNullPointer() {
            assertThrows(NullPointerException.class, () -> MoneyUtil.round(null));
        }
    }

    // ------------------------------------------------------------------
    // format
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("format - dinh dang vi-VN")
    class Format {

        @Test
        void wholeNumber_usesDotAsGroupSeparator() {
            // Phai la "500.000" (Viet Nam) chu khong phai "500,000" (US).
            // String.format("%,.0f") se ra sai dau cham o day.
            assertEquals("500.000", MoneyUtil.format(new BigDecimal("500000.00")));
        }

        @Test
        void withDecimals_usesCommaAsDecimalSeparator() {
            assertEquals("1.234.567,89", MoneyUtil.format(new BigDecimal("1234567.89")));
        }

        @Test
        void trailingZeros_areNotPadded() {
            // minFractionDigits = 0 nen "0,5" chu khong phai "0,50"
            assertEquals("0,5", MoneyUtil.format(new BigDecimal("0.50")));
        }

        @Test
        @DisplayName("4 chu so thap phan duoc giu lai - dung cho ty gia")
        void fourDecimalPlaces_keptForExchangeRate() {
            // Ty gia co 4 chu so thap phan - ly do da dinh dang "%,.0f" la bom no.
            // LUU Y: vi-VN dung COMMA cho phan thap phan, CHAM NGUYEN cho nhom.
            // Nen 12345.6789 ra "12.345,6789", khong phai "12,345.6789".
            assertEquals("12.345,6789", MoneyUtil.format(new BigDecimal("12345.6789")));
        }

        @Test
        void oneVnd_formatsWithoutScientificNotation() {
            assertEquals("1", MoneyUtil.format(new BigDecimal("1.00")));
        }
    }

    // ------------------------------------------------------------------
    // Tính hợp đồng giữa round và format
    // ------------------------------------------------------------------
    @Test
    @DisplayName("round roi format cho ra so tien doc duoc")
    void roundThenFormat_isReadable() {
        BigDecimal rounded = MoneyUtil.round(new BigDecimal("1234.5678"));

        assertEquals("1.234,57", MoneyUtil.format(rounded));
    }

    // ------------------------------------------------------------------
    // Ghi chú: KHONG test format(null)
    //
    // Da kiem chung: MoneyUtil.format(null) KHONG nem loi, ma tra ve
    // chuoi "null". Trong khi MoneyUtil.round(null) thi nem
    // NullPointerException. Hai ham cung xuat hien chuyen tien nhung
    // xu ly null khac nhau - xem ghi chu trong bao cao review.
    // ------------------------------------------------------------------

    @Test
    @DisplayName("RoundingMode la HALF_UP chu khong phai HALF_EVEN")
    void roundingMode_isHalfUp() {
        // Kiem tra bang HANH VI, khong phai doc source:
        // 2.665 -> HALF_UP ra 2.67, con HALF_EVEN se ra 2.66.
        // Hai quy tac khac nhau, tien se lech - nen phai chung mot quy tac.
        MoneyAssert.assertMoney("2.67", MoneyUtil.round(new BigDecimal("2.665")));
    }
}
