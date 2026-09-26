package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TypeTransactionTest {

    @Test
    @DisplayName("chuoi dung, dung chinh ta")
    void exactMatch_parses() {
        assertEquals(TypeTransaction.DEPOSIT, TypeTransaction.fromString("DEPOSIT"));
        assertEquals(TypeTransaction.WITHDRAW, TypeTransaction.fromString("WITHDRAW"));
        assertEquals(TypeTransaction.TRANSFER_OUT, TypeTransaction.fromString("TRANSFER_OUT"));
        assertEquals(TypeTransaction.TRANSFER_IN, TypeTransaction.fromString("TRANSFER_IN"));
    }

    @Test
    @DisplayName("chuoi thuong hoa thanh HOA")
    void lowerCase_isAccepted() {
        assertEquals(TypeTransaction.WITHDRAW, TypeTransaction.fromString("withdraw"));
    }

    @Test
    @DisplayName("co khoang trang o hai dau thi bo qua")
    void surroundingSpaces_areTrimmed() {
        assertEquals(TypeTransaction.DEPOSIT, TypeTransaction.fromString("  deposit  "));
    }

    @Test
    @DisplayName("gia tri la khong thi nem loi")
    void unknownValue_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> TypeTransaction.fromString("CHUYEN_KHOAN"));
    }

    @Test
    @DisplayName("chuoi rong thi nem loi, khong duoc tra null")
    void blank_throwsRatherThanReturningNull() {
        // Controller kiem tra isBlank() truoc nen o day khong xay ra.
        // Nhung neu chuyen "khong loc" thanh null thi phai xu ly o tang
        // tren - khong duoc de fromString() ngam hieu tra null.
        assertThrows(IllegalArgumentException.class,
                () -> TypeTransaction.fromString(""));
    }

    @Test
    @DisplayName("co 4 loai - them loai moi se phai sua ca bang xep hang")
    void enumHasExactlyFourValues() {
        // Nho them: sau nay them FEE/REVERSED, test nay se do ro
        // de nho cap nhat cho bao cao.
        assertEquals(4, TypeTransaction.values().length);
    }
}
