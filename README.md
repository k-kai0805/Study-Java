# Study-Java

Lộ trình học **Java từ số 0 → backend ngân hàng / cổng thanh toán**.
11 phase, tổng 12–18 tháng ở nhịp 5–10 giờ/tuần. Nguyên tắc xuyên suốt: **70% thực hành, 30% lý thuyết**.

[![Java CI](https://github.com/k-kai0805/Study-Java/actions/workflows/maven.yml/badge.svg)](https://github.com/k-kai0805/Study-Java/actions/workflows/maven.yml)
[![Java 21](https://img.shields.io/badge/Java-21-temurin-orange)](https://adoptium.net/)
[![Tests](https://img.shields.io/badge/tests-63%20passing-brightgreen)](#kiểm-thử)

> **Đây là repo học tập, đang xây dựng dần** — không phải hệ thống production.
> Hiện chứa một ứng dụng console mô phỏng nghiệp vụ ngân hàng, tập trung vào **thiết kế**:
> phân tách tầng, xử lý tiền tệ, sinh báo cáo, và kiểm thử tự động.

---

## Vì sao không chỉ là "code bài tập"

Phần lớn repo học tập chỉ dừng ở mức code chạy. Repo này tập trung vào **những quyết định
thiết kế có thể tranh luận được** — mỗi quyết định đều có lý do, và phần lớn đều có test
bảo vệ. Vài ví dụ:

### 1. Phân tách tầng rõ ràng, mỗi tầng một trách nhiệm

```
Controller  →  Service  →  DAO
  (I/O)        (quy tắc)   (truy vấn)
                    ↓
                 Report
          (gom nhóm + hiển thị)
```

Ba khái niệm hay bị lẫn và đây là cách phân biệt:

| | Là gì | Ở đâu | Ví dụ trong repo |
|---|---|---|---|
| **Business rule** | *Quyết định* — sai thì hệ thống làm hỏng việc | `Service` | Tiền ra gồm `{WITHDRAW, TRANSFER_OUT}` |
| **Derived data** | *Tính toán* — chỉ đọc, sai thì báo cáo sai | `Report` | Tổng khối lượng, bảng xếp hạng |
| **Query** | Lấy dữ liệu thô | `DAO` | `Bank.findAllTransactions(criteria)` |

`Bank` (DAO) cố tình **không** trả kết luận. Lý do: DAO không được biết trước sẽ có bao nhiêu
báo cáo cần dùng. Nếu DAO giao sẵn kết quả tổng hợp thì báo cáo thứ hai không dùng được, và
mỗi báo cáo mới lại phải sửa DAO — vi phạm Open/Closed. (Khi lên SQL, một câu
`SELECT … WHERE …` sẽ nuôi được cả ba báo cáo; `GROUP BY` làm ở tầng trên.)

### 2. `isOutflow` đặt ở Service, không ở Report, không ở enum

Định nghĩa "dòng tiền ra gồm những loại nào" nằm trong `EnumSet` private của
`AbstractBankService`, truy cập qua interface `IBankService`.

Đây là **quy tắc nghiệp vụ**, không phải quy tắc hiển thị. Báo cáo tổng hợp hôm nay cần nó,
báo cáo dòng tiền ròng ngày mai cũng cần — nếu mỗi báo cáo tự định nghĩa thì sẽ có N nơi định
nghĩa và các báo cáo cho ra những con số **mâu thuẫn nhau trên cùng một bộ dữ liệu**.

Đặt trong `enum TypeTransaction` cũng hợp lý (loại giao dịch tự biết mình là tiền vào/ra),
nhưng đó là *chính sách* có thể đổi theo quy định ngân hàng, không phải bản chất cố hữu —
mỗi lần đổi chính sách sẽ phải sửa cả model.

### 3. `closingBalance` ≠ số dư hiện tại

Cột "số dư" trong báo cáo là `closingBalance` — số dư **tại thời điểm chốt kỳ** — chứ không
phải `account.getBalance()` (số dư lúc bạn bấm nút).

Hai cái này **khác nhau**, và nếu nhầm thì báo cáo sai theo cách nguy hiểm nhất:

| Tình huống | Dùng `getBalance()` | Hậu quả |
|---|---|---|
| Xem kỳ quá khứ (tháng 1) trong tháng 9 | ra số dư **tháng 9** | Báo cáo ghi sai mốc thời gian, nhưng nhìn rất hợp lý, không ai phát hiện |
| Chạy lại cùng tiêu chí | ra số dư **khác** | Báo cáo không deterministic → không đối soát được |

Cách tính: `afterBalance` của giao dịch có `timestamp` muộn nhất **trong kỳ**.

Bẫy ở đây rất dễ mắc: phải xét **mọi** giao dịch trong kỳ, không chỉ dòng tiền ra. Nếu lấy
`afterBalance` của giao dịch tiền ra cuối cùng mà bỏ qua lệnh nhận tiền xảy ra sau đó, bạn ra
số dư **trung gian**. Repo có test khẳng định đúng cái bẫy này (`600.000`, không phải `500.000`).

Khi lên SQL, đây chính là lý do ngân hàng nào cũng có bảng `account_balance_history` — không
phải để lưu số dư hiện tại, mà để trả lời *"ngày hôm đó số dư là bao nhiêu"*.

### 4. Tiền tệ: `BigDecimal` tuyệt đối, và không bao giờ làm tròn âm thầm

- Mọi số tiền là `BigDecimal`. Không có `double`/`float` ở bất kỳ đâu.
- Định dạng vi-VN tập trung trong `MoneyUtil.format()`. **Không** dùng `String.format("%,.0f")`
  — `%.0f` ép về 0 chữ số thập phân, tức **làm tròn tiền không báo**. Đã kiểm chứng:
  `1234567.89` → in ra `1,234,568`.
- Số dư không âm: `withdraw` kiểm tra trước khi trừ, ném `InsufficientBalanceException`.

### 5. 63 test — và đã kiểm chứng test thật sự bắt được lỗi

Test xanh **không** chứng minh test có tác dụng. Vì vậy đã chạy **mutation test** — cố tình phá
code rồi xem test có đỏ không:

| Cố tình phá | Test bắt được |
|---|---|
| Thêm `DEPOSIT` vào danh sách tiền ra | 4 test đỏ ở 2 file |
| Tính `closingBalance` chỉ từ dòng tiền ra | 2 test đỏ |
| Đổi biên `fromDate` từ `>=` thành `>` | 3 test đỏ |

Test cũng dạy một bài học về chính nó: `BigDecimal.equals()` so sánh **cả scale**, nên
`assertEquals` với `BigDecimal` sẽ **đỏ dù code đúng**. Vì vậy có helper
`MoneyAssert.assertMoney(...)` dùng `compareTo() == 0`.

---

## Tiến độ

| Module | Nội dung | Trạng thái |
|---|---|---|
| Module 1 | Nghiệp vụ ngân hàng cốt lõi: deposit / withdraw / transfer, exception nghiệp vụ | ✅ |
| Module 2 | OOP: interface → abstract → impl, giảm ràng buộc, composition root | ✅ |
| Module 3.1 | Sao kê + bộ lọc theo loại / khoảng ngày | ✅ |
| Module 3.2 | Báo cáo đếm giao dịch theo từng loại | ✅ |
| Module 3.3 | Báo cáo tổng hợp: tổng khối lượng + top 5 tài khoản theo tiền ra (AML sơ bộ) | ✅ |
| Module 3.4 | Chuẩn hoá 3 họ report về một interface + menu | 📦 đang làm |
| **Phase 4** | I/O, xuất CSV, BigDecimal chuyên sâu, `java.time` | ⏭️ kế tiếp |

**Hạ tầng đã dựng:** Maven + Maven Wrapper · GitHub Actions CI (cache dependency) ·
`.gitignore` sạch · 63 unit test.

---

## Cấu trúc dự án

```
src/
├── main/java/
│   ├── Main.java                    composition root: dựng & nối dependency
│   ├── controller/                  đọc input, điều phối, bắt exception
│   ├── service/                     business rule + giao dịch
│   ├── repo/                        DAO — truy vấn, trả dữ liệu thô
│   ├── report/                      sao kê (1 tài khoản)
│   ├── countTransaction/            đếm theo loại (1 tài khoản)
│   ├── analyst/                     tổng hợp + top 5 (toàn hệ thống)
│   ├── model/                       Transaction, Account, TransactionCriteria
│   │   └── dto/                     OutflowSummary — dữ liệu đã gom nhóm
│   ├── exception/                   exception nghiệp vụ
│   └── utils/                       MoneyUtil (tiền tệ), MessageConstants
│
└── test/java/                       63 test
    ├── support/MoneyAssert          so sánh BigDecimal đúng cách
    ├── service/                     business rule
    ├── repo/                        lọc dữ liệu
    ├── analyst/                     logic gom nhóm báo cáo
    ├── utils/                       làm tròn & định dạng
    └── model/                       parse enum
```

Ba họ report cùng theo pattern **interface → abstract → impl**:
`AbstractX` lo truy vấn + gom nhóm, `X` (impl) lo định dạng + in ra.
Test logic gom nhóm mà **không sửa code production** — bằng cách subclass trong test để bắt
tham số truyền vào hook `render()`.

---

## Chạy thử

Yêu cầu: **JDK 21**.

```bash
git clone git@github.com:k-kai0805/Study-Java.git
cd Study-Java

export JAVA_HOME=$(/usr/libexec/java_home -v 21)   # macOS
./mvnw clean verify

java -cp target/classes Main
```

Dùng `./mvnw` (Maven Wrapper) chứ không dùng `mvn` — wrapper pin sẵn phiên bản Maven 3.9.16
nên máy của bạn và CI luôn chạy cùng phiên bản.

> **Lưu ý macOS:** `brew install maven` có thể kéo theo OpenJDK mới hơn. Hãy set `JAVA_HOME`
> như trên để khớp với CI.

Ứng dụng console chạy được ngay với dữ liệu mẫu (8 tài khoản). Menu gồm: xem số dư, nạp,
rút, chuyển khoản, danh sách tài khoản, và 3 loại báo cáo.

Ví dụ báo cáo tổng hợp:

```
Total Volume: 2.500.000 VND
Top 5 Outflow Accounts:
Account         Outflow              Closing Balance
ACC10001        500.000 VND          2.600.000 VND
ACC10002        100.000 VND          700.000 VND
```

## Kiểm thử

```bash
./mvnw test
```

63 test, chia theo tầng kiến trúc — ưu tiên test **business rule** (chỗ sai là mất tiền
thật) hơn test hàm dễ. Chạy tự động trên GitHub Actions mỗi lần push.

## Công nghệ

| | |
|---|---|
| Ngôn ngữ | Java 21 (Temurin) |
| Build | Maven 3.9.16 + Maven Wrapper |
| Test | JUnit 5 |
| CI | GitHub Actions (`ubuntu-latest`, cache dependency) |
| Thư viện | Lombok — **duy nhất**, và chỉ ở scope `provided` |

Cố ý giữ dependency tối thiểu. Chưa thêm Spring Boot / JDBC driver / Jackson vì mỗi thứ
đúng lúc đó sẽ phải mới cần. Chưa có gì thừa: `./mvnw dependency:analyze` sẽ phát hiện
dependency không dùng.

---

## Lộ trình chi tiết

Bản roadmap đầy đủ 11 phase — kèm lý thuyết tối thiểu, bài tập nghiệp vụ, ví dụ thực tế và
checklist đạt chuẩn cho từng phase:

**[`docs/roadmap.md`](docs/roadmap.md)**

Tóm tắt:

| Phase | Nội dung | Tuần |
|---|---|---|
| 4 | I/O, Serialization, BigDecimal, `java.time` | 23–27 |
| 5 | SQL + JDBC, transaction, ACID | 28–35 |
| 6 | Multithreading & Concurrency — chống mất tiền do race condition | 36–42 |
| 7 | Networking, HTTP, giới thiệu ISO 8583 | 43–46 |
| 8 | **Spring Boot + JPA + Transaction** | 47–60 |
| 9 | Security + Production: JWT, AES, HMAC, PCI-DSS | 61–72 |
| 10 | Redis, Kafka, Docker | 73–80 |
| 11 | Capstone: mini payment gateway | 81–90 |

Hai vấn đề kỹ thuật đã ghi nhận, sẽ xử lý đúng lúc:

- **`NumberFormat` không thread-safe.** `MoneyUtil.FORMATTER` hiện là static dùng chung. An toàn
  với console đơn luồng, nhưng phải tách trước khi vào **Phase 6** — hai thread format tiền
  sẽ ra kết quả lẫn nhau.
- **Mốc thời gian giao dịch có thể trùng.** `LocalDateTime.now()` có độ phân giải hữu hạn, hai
  lệnh phát sinh rất nhanh có thể trùng timestamp → "giao dịch cuối cùng trong kỳ" không xác
  định. Cần mã chứng từ tăng dần (`AtomicLong`) — đúng bài tập ở Phase 6.
