package model;

public enum TypeTransaction {
    DEPOSIT,
    WITHDRAW,
    TRANSFER_OUT,
    TRANSFER_IN;

    public static TypeTransaction fromString(String value) {
        return switch (value.trim().toUpperCase()) {
            case "DEPOSIT" -> DEPOSIT;
            case "WITHDRAW" -> WITHDRAW;
            case "TRANSFER_OUT" -> TRANSFER_OUT;
            case "TRANSFER_IN" -> TRANSFER_IN;
            default -> throw new IllegalArgumentException(
                    "Invalid transaction type: " + value);
        };
    }
}
