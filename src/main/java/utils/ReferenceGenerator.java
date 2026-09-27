package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public final class ReferenceGenerator {

    private static final AtomicLong SEQUENCE =
            new AtomicLong(1);

    private ReferenceGenerator() {
    }

    public static String nextReference() {
        long seq = SEQUENCE.incrementAndGet();

        return "TXN-%s-%06d".formatted(
                LocalDate.now()
                        .format(DateTimeFormatter.BASIC_ISO_DATE),
                seq
        );
    }
}
