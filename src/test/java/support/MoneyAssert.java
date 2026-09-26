package support;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Assertion chuyen bien cho tien.
 *
 * <p>Ly do ton tai: {@code BigDecimal.equals()} so sanh CA SCALE, nen
 * {@code 2500000.00} khac {@code 2500000} du gia tri bang nhau. {@code assertEquals}
 * cua JUnit goi {@code equals} -> test do ro du code dung. Va neu "Sua" bang cach
 * bo di ".00" thi test lai xanh nhung da khong con kiem tra scale - tuc la
 * che mat loi tien te.
 *
 * <p>Dung {@code compareTo() == 0} moi so sanh dunggia tri.
 */
public final class MoneyAssert {

    private MoneyAssert() {
    }

    public static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0,
                new BigDecimal(expected).compareTo(actual),
                () -> "expected " + expected + " but was " + actual);
    }

    public static void assertMoney(String expected, BigDecimal actual, String message) {
        assertEquals(0,
                new BigDecimal(expected).compareTo(actual),
                message + " | expected " + expected + " but was " + actual);
    }
}
