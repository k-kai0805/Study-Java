package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public final class MoneyUtil {

    // Set standard scale for monetary operations (usually 2 decimal places)
    private static final int DEFAULT_SCALE = 2;
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;

    private static final NumberFormat FORMATTER =
            NumberFormat.getNumberInstance(
                    new Locale("vi", "VN")
            );

    static {
        FORMATTER.setMinimumFractionDigits(0);
        FORMATTER.setMaximumFractionDigits(4);
    }

    /**
     * Rounds a BigDecimal amount to 2 decimal places using HALF_UP rounding.
     */
    public static BigDecimal round(BigDecimal value) {
        return Objects.requireNonNull(value, "value must not be null").setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }
    public static String format(BigDecimal amount) {
        return FORMATTER.format(amount);
    }
}
