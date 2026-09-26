# LỘ TRÌNH HỌC TỪ SỐ 0 → BACKEND NGÂN HÀNG / PAYMENT GATEWAY (JAVA)

> **Hồ sơ người học:** Hoàn toàn bắt đầu từ con số 0.
> **Thời gian:** 5–10 giờ/tuần (mục tiêu 12–18 tháng để đi làm được).
> **Mục tiêu nghề nghiệp:** Lập trình viên backend làm việc trong hệ thống **ngân hàng** hoặc **cổng thanh toán (payment gateway)**.
> **Nguyên tắc xuyên suốt:** 70% thực hành + 30% lý thuyết. Không được bỏ "Ví dụ nghiệp vụ" – đó chính là phần bạn sẽ gặp khi đi làm.

---

## ĐANG Ở ĐÂU

**Phase 3 — Collections, Generics, Exception.** Xem [`../README.md`](../README.md) để biết
tiến độ chi tiết theo từng module.

Dưới đây là lộ trình đầy đủ 11 phase, kèm phần lý thuyết và bài tập của từng phase. **Checklist
cuối mỗi phase là điều kiện để được sang phase kế tiếp — không nhảy cóc.**

| Ký hiệu | Nghĩa |
|---|---|
| ✅ | Đã hoàn thành |
| ⬜ | Chưa làm |

---

## CÁCH DÙNG LỘ TRÌNH NÀY

- Học lần lượt từng Phase, **không nhảy cóc**.
- Mỗi phase có: **Mục tiêu → Lý thuyết tối thiểu → Bài tập nghiệp vụ → Ví dụ thực tế → Checklist đạt chuẩn**.
- Chỉ được chuyển phase khi **pass checklist cuối phase**.
- Tiếng Anh kỹ thuật: gặp từ chưa biết thì tra ngay `https://www.google.com/search?q=<từ>+meaning+in+programming`. Khi đi làm, tài liệu chính thức của ngân hàng nhiều khi viết bằng tiếng Anh.

---

## TỔNG QUAN 11 PHASE

| Phase | Tên | Tuần ước tính | Mốc đạt được |
|-------|-----|----------------|--------------|
| 1 | Java Core căn bản | 1–8 | Viết chương trình console xử lý nghiệp vụ |
| 2 | OOP | 9–16 | Mô hình hóa tài khoản/giao dịch |
| 3 | Collections, Generics, exception | 17–22 | Quản lý lịch sử giao dịch, tra soát |
| 4 | I/O, Serialization, BigDecimal | 23–27 | Xuất sao kê, xử lý tiền tệ an toàn |
| 5 | SQL + JDBC | 28–35 | Lưu trữ và truy vấn dữ liệu giao dịch |
| 6 | Multithreading & Concurrency | 36–42 | Xử lý giao dịch đồng thời, chống mất tiền |
| 7 | Networking + HTTP | 43–46 | Gọi API cổng thanh toán |
| 8 | Spring Boot + JPA + Transaction | 47–60 | Xây API ngân hàng/thanh toán hoàn chỉnh |
| 9 | Security + Production | 61–72 | Bảo mật, ISO 8583, chuẩn PCI-DSS |
| 10 | Mở rộng: Redis, Kafka, Docker | 73–80 | Hệ thống cao cấp |
| 11 | Capstone Project | 81–90 | Mini payment gateway để deploy + để trong CV |

> Tuần là con số tham chiếu theo nhịp ~6h/tuần. Nếu học 10h/tuần thì rút ngắn tương ứng.

---

# PHASE 1 — JAVA CORE CĂN BẢN (Tuần 1–8)

## Mục tiêu
- Cài đặt môi trường: JDK 17 (hoặc 21 LTS) + IntelliJ IDEA (Community bản miễn phí).
- Thuần thục: biến, kiểu dữ liệu, toán tử, `if`/`switch`, vòng lặp, `array`, `String`.
- Hiểu cách chương trình chạy từng dòng (debug bằng IntelliJ).

## Lý thuyết tối thiểu
- Cú pháp Java: `class`, `main`, in ra màn hình.
- Kiểu dữ liệu: `int`, `long`, `double`, `boolean`, `char`, `String`.
- **Tuyệt đối cấm dùng `float`/`double` để tính tiền** — chỉ học để biết, nguyên nhân sẽ rõ ở Phase 4.
- Nhập dữ liệu từ bàn phím bằng `Scanner`.
- Vòng lặp `for`, `while`, `do-while`.
- Mảng `int[]`, mảng `String[]`.

## Bài tập nghiệp vụ (làm tất cả)
1. **ATM rút tiền:** Nhập số dư và số tiền muốn rút. Kiểm tra:
   - Tiền phải là bội số của 50.000 VND.
   - Số dư phải đủ.
   - In thông báo kết quả phù hợp từng trường hợp.
2. **Tính lãi suất tiết kiệm:** Nhập số tiền gửi + số tháng. Lãi suất theo bậc (dùng `if`):
   - < 6 tháng: 3.5%/năm, 6–12 tháng: 5%/năm, > 12 tháng: 6%/năm.
   - In số tiền lãi và tổng nhận về. Ví dụ: 10.000.000 VND, 6 tháng → 250.000 VND lãi.
3. **Kiểm tra mã thẻ (Luhn):** Viết hàm kiểm tra 16 số thẻ hợp lệ bằng thuật toán Luhn (dùng `for` + tách chữ số). Đây là cách các ngân hàng kiểm tra nhanh thẻ trước khi gọi mạng thanh toán.
4. **Danh sách 10 giao dịch (mảng):** Cho mảng 10 số tiền giao dịch trong ngày. Tính tổng, số giao dịch lớn hơn 1 triệu, và số giao dịch nhỏ nhất.
5. **Bảng lãi kép (vòng lặp lồng):** In bảng số tiền tích lũy khi gửi 10 triệu với lãi 5%/năm trong 5 năm (lần lượt các năm).

## Ví dụ nghiệp vụ thực tế
- Khi bạn chuyển khoản trên app ngân hàng, app phải **kiểm tra số dư trước khi khớp lệnh** — đúng logic bài ATM.
- Mã 16 số thẻ Visa/Mastercard (bạn cầm trên tay hoặc trong ví điện tử) hợp lệ theo chuẩn **ISO/IEC 7812**, lớp kiểm tra đầu tiên chính là thuật toán **Luhn** (bài 3).
- Tiền tệ VND luôn là **bội số của 50 VND** trở lên (đồng tiền tối thiểu 500 VND không còn lưu hành, mệnh giá phổ biến 50k/100k/200k/500k) — bài 1 minh họa đúng thực tế NHNN.

## Checklist đạt chuẩn (pass mới sang Phase 2)
- [x] Cài được JDK + IntelliJ, chạy được chương trình `Hello`.
- [x] Giải thích được sự khác biệt `int`/`long`/`double` cho người khác nghe.
- [ ] Tự viết được 5 bài tập trên mà không nhìn đáp án mẫu.
- [ ] Dùng được debug của IntelliJ để xem từng bước.

---

# PHASE 2 — OOP (Tuần 9–16)

## Mục tiêu
- Nắm vững 4 tính chất: đóng gói, kế thừa, đa hình, trừu tượng.
- Biết dùng `enum`, `interface`, `abstract class`.
- Bước đầu làm quen **Design Pattern** cơ bản: Factory, Strategy.

## Lý thuyết tối thiểu
- `class`, `object`, `constructor`, `getter`/`setter`, `this`.
- `static` (biến tĩnh), hằng số (dùng cho giá trị lãi suất chuẩn, phí giao dịch).
- `extends` (kế thừa), `@Override`.
- `interface` — **rất quan trọng trong hệ thống thanh toán** vì cho phép tích hợp nhiều ngân hàng/loại thẻ.
- `enum` — dùng cho trạng thái giao dịch, loại tài khoản.

## Bài tập nghiệp vụ (làm tất cả)
1. **Mô hình tài khoản:** `class Account` (số tài khoản, tên chủ, số dư `BigDecimal` sơ bộ). Tạo `SavingsAccount extends Account` (thêm lãi suất, hàm `addMonthlyInterest()`) và `CheckingAccount extends Account` (thêm phí chuyển tiền nội bộ).
2. **Handler giao dịch theo đa hình:** Tạo interface `TransactionHandler` với method `process(amount)`. Cài 3 phiên bản: `TransferHandler` (chuyển khoản), `WithdrawalHandler` (rút tiền), `DepositHandler` (nộp tiền). Dùng **Strategy pattern**: chọn handler đúng theo **loại giao dịch (`enum TransactionType`)**.
3. **Factory tạo tài khoản:** `AccountFactory` trả về `SavingsAccount` hay `CheckingAccount` dựa trên loại. Đảm bảo: số tài khoản được sinh tự động, duy nhất (loại tài khoản + số thứ tự).
4. **Trạng thái giao dịch bằng enum:** `enum TransactionStatus { PENDING, PROCESSING, SUCCESS, FAILED, REVERSED }`. Viết lớp theo dõi trạng thái và chỉ cho phép chuyển trạng thái hợp lệ (VD: SUCCESS không được quay về PENDING). Đây là nền tảng của **trạng thái giao dịch ngân hàng thực tế**.
5. **Tính tổng phí đa hình:** Mảng các giao dịch, mỗi giao dịch là `Transaction` (interface có `getFee()`), in tổng phí.

## Ví dụ nghiệp vụ thực tế
- Các ngân hàng VN dùng `interface` chuẩn để tích hợp **NAPAS** cho chuyển tiền khác ngân hàng; mỗi ngân hàng là một "implementation". Khi thêm ngân hàng mới, chỉ cần thêm class mới — **không sửa code cũ** (nguyên lý Open/Closed).
- Trạng thái giao dịch trong app banking: `PENDING` (chờ xác thực OTP), `PROCESSING` (ngân hàng đang xử lý), `SUCCESS/FAILED`, `REVERSED` (hoàn tiền). Ví dụ bài 4 mô phỏng đúng chuỗi trạng thái này.

## Checklist đạt chuẩn
- [x] Giải thích lại được kế thừa vs interface, khi nào dùng cái nào.
- [ ] Tự vẽ được "biểu đồ lớp" cho bài Account mà không cần giúp đỡ.
- [x] Hiểu vì sao `interface` giúp hệ thanh toán mở rộng dễ dàng.

---

# PHASE 3 — COLLECTIONS, GENERICS, EXCEPTION (Tuần 17–22)

## Mục tiêu
- Thành thạo `ArrayList`, `LinkedList`, `HashSet`, `HashMap`, `TreeMap`.
- Dùng `Comparable`/`Comparator` để sort; sử dụng `generics`.
- Xây dựng hệ thống exception rõ ràng cho nghiệp vụ ngân hàng.

## Lý thuyết tối thiểu
- `List` vs `Set` vs `Map`: đặc điểm và khi nào dùng cái nào.
- Lặp collection bằng `for-each`, `iterator`.
- Nhóm/xử lý dữ liệu: `Collections.sort`, `Collections.frequency`, `Collections.unmodifiableList`.
- `Comparable` (sắp theo kiểu tự nhiên) vs `Comparator` (sắp theo tiêu chí bất kỳ).
- exception: `try-catch-finally`, `throw`, `throws`; checked vs unchecked; **custom exception**.

## Bài tập nghiệp vụ (làm tất cả)
1. **Lịch sử giao dịch:** `TransactionHistory` quản lý `List<Transaction>`.
   - Thêm, xem, xóa giao dịch theo số tài khoản.
   - Tra cứu "5 giao dịch gần nhất" (sort theo thời gian giảm dần).
2. **Tra soát giao dịch:** Cho `Map<String, Transaction> giaoDịchTheoMã` (key = mã giao dịch).
   - Tìm giao dịch chỉ với 1 truy vấn `O(1)`.
   - Đếm số giao dịch theo từng loại (chuyển/nạp/rút), in bảng.
3. **Kiểm tra trùng lặp:** Dùng `HashSet` phát hiện **mã giao dịch trùng lặp** khi import file từ chi nhánh — đây là bài toán chống trùng lệnh trong ngân hàng.
4. **Sắp xếp danh sách khách hàng:** Dùng `Comparator` sắp theo (a) số dư giảm dần, (b) tên A→Z. Lưu ý so sánh **số dư bằng BigDecimal** không phải double.
5. **Custom exception nghiệp vụ:** Định nghĩa `InsufficientBalanceException`, `AccountNotFoundException`, `InvalidTransactionAmountException`. Trong `BankService`, ném đúng exception cho đúng trường hợp. Bắt exception ở nơi gọi và in thông điệp thân thiện.

## Ví dụ nghiệp vụ thực tế
- Khi ngân hàng nhận file sao kê/kinh doanh từ một kênh (Mobile Banking, ATM, Internet Banking), phải kiểm tra **trùng mã chứng từ** bằng `HashSet` trước khi hạch toán — tránh "phát sinh lệnh trùng", là lỗi bị phạt NATO (nghiêm trọng trong thanh toán, bị thanh tra NHNN nhắc).
- Tra soát giao dịch trên hotline: khách gọi điện tra soát, nhân viên cần tìm đúng giao dịch theo mã — `Map` key mã giao dịch là giải pháp.
- exception research: số dư không đủ là bài toán trả về lỗi "GDKH không đủ số dư" trên app ngân hàng; mỗi mã lỗi chuẩn hóa (VD: `EBAL-001`).

## Checklist đạt chuẩn
- [x] Chọn đúng loại collection cho từng tình huống và giải thích được.
- [ ] Tự viết được 5 bài tập trên độc lập. — **4/5 xong.**
- [x] Phân biệt rõ checked vs unchecked exception.

> **Còn thiếu 1 bài tập của Phase 3: kiểm tra trùng lặp bằng `HashSet`** (phát hiện trùng mã
> chứng từ khi nhập giao dịch từ chi nhánh — bài toán chống lệnh trùng trong thanh toán).
> `HashSet` hiện chưa xuất hiện trong code. Cũng là bước hợp lý kế tiếp: `Transaction` chưa có
> mã chứng từ, nên cần thêm `String referenceNo` vào record trước, rồi dùng `HashSet` chặn
> trùng khi nạp lô.
>
> Các mục để trống còn lại là những mục **tự đánh giá** (làm bài tập riêng, dùng debug, vẽ biểu
> đồ lớp) — không có bằng chứng trong repo nên không đánh dấu.

---

# PHASE 4 — I/O, SERIALIZATION, BIGDECIMAL, DATE/TIME (Tuần 23–27)

## Mục tiêu
- Đọc/ghi file, xuất báo cáo, làm việc với JSON.
- **Trọng tâm:** thành thạo `BigDecimal` để xử lý tiền — đây là kiến thức "bắt buộc có" khi phỏng vấn ngân hàng.
- Dùng `java.time` (LocalDate, LocalDateTime) không dùng `Date` cũ.

## Lý thuyết tối thiểu
- `File`, `BufferedReader`, `BufferedWriter`, `try-with-resources`.
- JSON: làm quen với **Jackson** (hoặc Gson) — đọc/ghi object ra JSON.
- `BigDecimal`: `new BigDecimal("10.5")` (không dùng constructor từ double!), `add`, `subtract`, `multiply`, `setScale(2, RoundingMode.HALF_UP)`.
- **Vì sao không dùng double cho tiền:** `0.1 + 0.2 != 0.3` trong nhị phân → lệch hàng phân → mất tiền khi tích lũy. Ngân hàng dùng số dư có **tối đa 2 chữ số thập phân** (đôi khi 4 cho tỷ giá).
- `LocalDate`, `LocalDateTime`, tính chênh lệch ngày, so sánh.

## Bài tập nghiệp vụ (làm tất cả)
1. **Xuất sao kê CSV:** `StatementGenerator` ghi `sao_ke.csv` gồm: số tài khoản, ngày, mô tả, số tiền, số dư sau giao dịch. Mở bằng Excel được.
2. **Đọc file giao dịch:** Đọc danh sách giao dịch từ file (CSV hoặc JSON), parse về `List<Transaction>`. Kiểm tra dòng lỗi và bỏ qua + ghi log dòng không hợp lệ.
3. **Máy tính phí chuyển tiền với BigDecimal:** Phí = 0.5% số tiền (tối thiểu 5.000 VND, tối đa 100.000 VND), làm tròn 2 chữ số theo `HALF_UP`. In ra bảng phí - đảm bảo kết quả chính xác tuyệt đối.
4. **Số dư tối thiểu:** Cho lãi suất tích lũy theo ngày (ngày nào có tiền đủ 1 ngày thì tính lãi). Tính tổng lãi tháng bằng `BigDecimal` tích lũy — bài toán thực tế của kế toán tiền gửi.
5. **Ngày kết toán / giá trị (value date):** Tính **ngày giá trị** cho giao dịch chuyển khoản sau T+0/T+1 (làm việc với `LocalDate`, loại cuối tuần/ngày lễ — tạm dùng danh sách ngày lễ cố định).

## Ví dụ nghiệp vụ thực tế
- Mọi thông tin tiền tệ trong core banking (VD: hệ thống **TCBS/TPBank, Vietcombank CoreBanking** kiểu Temenos) đều là **số thập phân cố định chữ số**, không phải double.
- Sao kê CSV/PDF khách nhận được hàng tháng được sinh từ dữ liệu giao dịch — đúng bài 1.
- Phí chuyển khoản liên ngân hàng qua NAPAS (5.000–100.000 VND theo NAPAS quy định) là bài 3.

## Checklist đạt chuẩn
- [ ] Trả lời được câu hỏi phỏng vấn kinh điển: "Vì sao không dùng double để tính tiền?"
- [ ] Tự xuất được file CSV mở bằng Excel.
- [ ] Xử lý được phép cộng/trừ/so sánh tiền luôn đúng 2 chữ số thập phân.

---

# PHASE 5 — SQL + JDBC (Tuần 28–35)

## Mục tiêu
- Thiết kế bảng dữ liệu tài khoản/giao dịch đúng chuẩn.
- Viết truy vấn `SELECT/JOIN/GROUP BY` thành thạo.
- Dùng **Transaction SQL** để đảm bảo tính toàn vẹn — nền tảng của chuyển khoản.

## Lý thuyết tối thiểu
- Cài **MySQL/PostgreSQL** (PostgreSQL khuyến khích vì ~ngân hàng). Tool: DBeaver miễn phí.
- DDL: `CREATE TABLE`, constraints (`PRIMARY KEY`, `FOREIGN KEY`, `UNIQUE`, `CHECK`, `NOT NULL`).
- DML: `INSERT`, `UPDATE`, `DELETE`, `SELECT`, `WHERE`, `ORDER BY`, `LIMIT`.
- `JOIN` (INNER/LEFT), `GROUP BY`, `HAVING`, `COUNT/SUM/AVG`.
- Index: khi nào cần; tạo index trên số tài khoản, mã giao dịch, ngày.
- **ACID:** Atomicity, Consistency, Isolation, Durability — giải thích bằng ví dụ chuyển khoản.
- JDBC: `Connection`, `PreparedStatement` (**chống SQL injection — bắt buộc**), `ResultSet`, `transaction` (`setAutoCommit(false)`).

## Bài tập nghiệp vụ (làm tất cả)
1. **Schema ngân hàng:**
   - `customer(id, full_name, cccd, phone)` với `UNIQUE(cccd)`.
   - `account(id, customer_id FK, account_number UNIQUE, balance NUMERIC(15,2), status)`.
   - `transaction(id, account_id FK, amount NUMERIC(15,2), type, status, note, created_at)` với `CHECK(amount > 0)`.
   - `CHECK(balance >= 0)` — số dư không âm.
2. **Chuyển khoản 2 giai đoạn (SQL transaction):**
   - `BEGIN; UPDATE` trừ số dư người gửi → `UPDATE` cộng người nhận → `INSERT` 2 dòng giao dịch → `COMMIT;` (hoặc `ROLLBACK` nếu lỗi).
   - Minh họa: nếu bước giữa lỗi, không có bước nào được áp dụng.
3. **Báo cáo tổng hợp:** Với dữ liệu giả (chèn 100 dòng bằng script), viết query: tổng số tiền giao dịch mỗi ngày, top 5 khách có tổng giao dịch lớn nhất, số lượng giao dịch theo loại.
4. **Tìm giao dịch nghi vấn:** Qua query: khách có tổng giao dịch lớn hơn 100 triệu trong 1 ngày (dấu hiệu **rửa tiền** — AML). Để dành cho Phase 9.
5. **JDBC CRUD:** Viết `AccountDao` + `TransactionDao` bằng JDBC (`PreparedStatement`), kiểm thử bằng hàm `main`. Không dùng bất kỳ framework nào ở phase này — để hiểu gốc.

## Ví dụ nghiệp vụ thực tế
- Chuyển khoản liên ngân hàng NAPAS thực chất là chuỗi transaction đảm bảo **atomic**: hoặc trừ tiền người gửi + cộng tiền người nhận đều xong, hoặc không gì xảy ra. Nếu giữa chừng lỗi mà vẫn hiện "thành công" là sự cố mất tiền — ngân hàng phải làm **tra soát, đối soát hoàn trả**.
- `balance NUMERIC(15,2)` đúng chuẩn lưu tiền: tối đa 10^15 đồng (1 triệu tỷ) với 2 chữ số thập phân; số dư âm là vi phạm hệ thống.
- Cảnh báo giao dịch bất thường (AML) tự động chạy bằng query kiểu bài 4 — ngân hàng có hệ thống phát hiện rửa tiền theo quy định NHNN.

## Checklist đạt chuẩn
- [ ] Tạo được schema trên mà không cần nhìn.
- [ ] Giải thích được ACID bằng ví dụ chuyển tiền.
- [ ] Viết được chuyển khoản transaction bằng JDBC thuần.
- [ ] Lý giải tại sao luôn dùng `PreparedStatement`.

---

# PHASE 6 — MULTITHREADING & CONCURRENCY (Tuần 36–42)

## Mục tiêu
- Hiểu Thread, race condition, từ đó giải bài toán **giao dịch đồng thời** — gốc rễ của ngân hàng.
- Biết các kỹ thuật: `synchronized`, `AtomicInteger`, khóa `ReentrantLock`, thread-safe collections.

## Lý thuyết tối thiểu
- `Thread`, `Runnable`, `ExecutorService` + `Future`.
- Race condition: hai thread cùng đọc/ghi 1 biến.
- `synchronized` (method/block), `AtomicInteger`, `ReentrantLock`.
- `ConcurrentHashMap`, `CopyOnWriteArrayList`.
- **Deadlock** và cách tránh (khóa theo thứ tự).

## Bài tập nghiệp vụ (làm tất cả)
1. **Mô phỏng chuyển tiền đồng thời:** 1 tài khoản số dư 1 tỷ. 1000 thread mỗi thread chuyển 1 triệu. Sau khi xong, số dư phải **khớp tuyệt đối** (1 tỷ − 1000×1 triệu − phí). Nếu dùng code không an toàn (không lock), chỉ ra chỗ số dư bị lệch và giải thích vì sao.
2. **Cả 2 người đều đúng về mình:** Sự cố kinh điển — 2 người cùng muốn "khóa" 2 tài khoản theo thứ tự ngược nhau → deadlock. Viết chương trình tái hiện deadlock, rồi sửa bằng **khóa theo thứ tự cố định** (VD: luôn khóa số tài khoản nhỏ trước).
3. **Bộ đếm thẻ POS:** Dùng `AtomicLong` cho số thứ tự giao dịch phát sinh song song nhiều request, đảm bảo không trùng mã chứng từ.
4. **Batch thanh toán đa luồng:** `ExecutorService` xử lý 10.000 giao dịch trong cùng 1 file thanh toán (lương cho công ty), tăng tốc so với đơn luồng; ghi lại thời gian chạy.
5. **Bảng cân đối theo thời gian thực:** `ConcurrentHashMap<String, Account>` + `compute` để cập nhật số dư atomic không cần `synchronized` bao ngoài.

## Ví dụ nghiệp vụ thực tế
- Cốt lõi hệ điều hành thanh toán: khi 2 request cùng chuyển tiền từ 1 tài khoản, hệ thống phải **khóa tài khoản** (row lock trong DB) — nếu không có khóa, ví điện tử có thể bị "âm tiền" khi khuyến mãi (sự cố "sập khuyến mãi" của nhiều ví từng xảy ra).
- Deadlock có thể xảy ra khi đối soát liên ngân hàng đồng thời theo 2 chiều; ngân hàng quy định **thứ tự khóa chuẩn** để tránh.
- Mã chứng từ giao dịch không được trùng (bài 3) là yêu cầu **bắt buộc** để đối soát (NAPAS đối soát theo mã chứng từ).

## Checklist đạt chuẩn
- [ ] Giải thích được race condition và cách khắc phục.
- [ ] Viết được credit/debit an toàn chạy 1000 luồng, số dư khớp.
- [ ] Nhận biết được deadlock và tránh được nó.

---

# PHASE 7 — NETWORKING + HTTP & API CƠ BẢN (Tuần 43–46)

## Mục tiêu
- Hiểu mô hình client-server, HTTP request/response.
- Gọi/test API bằng Postman, đọc JSON.
- Giới thiệu chuẩn tin nhắn **ISO 8583** (nền tảng giao dịch thẻ/ATM).

## Lý thuyết tối thiểu
- TCP vs UDP; cổng (port), localhost.
- HTTP: method GET/POST/PUT/DELETE, status code (200, 400, 401, 403, 404, 500), header, body.
- REST: tài nguyên + URL; phân biệt bảo mật GET vs POST (GET lộ data lên URL/log).
- JSON cơ bản: `{}`, `[]`, kiểm tra bằng công cụ (khi làm việc thật sẽ dùng Jackson).
- **ISO 8583 giới thiệu:** tin nhắn gồm MTI + bitmap + các trường từ trường 2→128 (VD: trường 2 = số thẻ, trường 4 = số tiền, trường 22 = loại giao dịch). Hiểu để biết "thế giới ATM/thẻ nói bằng gì".

## Bài tập nghiệp vụ (làm tất cả)
1. **Mô phỏng "cổng thanh toán" bằng HTTP:** Dùng Postman gọi API giả lập (có thể là mock server, hoặc API do bạn tự viết sau Phase 8): POST `/api/v1/payment/transaction` gửi JSON `{ "cardNo": "...", "amount": 500000, "merchant": "TGDĐ" }`, nhận JSON phản hồi chứa mã giao dịch.
2. **Quiz về status code:** Diễn giải đúng từng mã: 200/201/400/401/403/404/422/500/503 cho các tình huống thanh toán (thẻ hết hạn, số dư không đủ, token hết hạn, server đang bảo trì).
3. **Đọc "bản tin" ISO 8583 (giả lập trước):** Tuần từng tháng, chương trình Java đọc chuỗi (giả) dạng ISO 8583 và in ra: loại giao dịch (lấy từ MTI), số thẻ (trường 2, 3-19), số tiền (trường 4, 12/13 chữ số cuối). Dùng substring — bản demo trước khi học thật ở Phase 9.
4. **Cổng TCP nhỏ:** Viết server TCP (SocketServer) lắng nghe cổng, nhận số tiền và trả lời "GIAO-DICH-THANH-CONG". Dùng để hình dung giao dịch ATM offline thời xưa (giờ đã dùng mạng mới).

## Ví dụ nghiệp vụ thực tế
- Khi bạn quẹt thẻ/gọi QR, cổng thanh toán (VD: **VNPay, Momo, Napas**) nhận request theo chuẩn riêng, đổi sang ISO 8583 để gọi sang **mạng thẻ quốc tế** (Visa/Mastercard) hoặc NAPAS, rồi trả kết quả về.
- Status code 401 chính là lỗi khi token hết hạn trên app ngân hàng bạn phải đăng nhập lại.

## Checklist đạt chuẩn
- [ ] Gọi được API bằng Postman và giải thích request/response.
- [ ] Đọc được cấu trúc cơ bản của tin nhắn ISO 8583.
- [ ] Phân biệt được khi nào dùng mạng HTTP vs socket TCP.

---

# PHASE 8 — SPRING BOOT + JPA + TRANSACTION (Tuần 47–60) ⭐ TRỌNG TÂM LỚN NHẤT

> Đây là phase quyết định để được tuyển dụng làm backend ngân hàng. Học thật chậm, làm thật kỹ.

## Mục tiêu
- Xây được **API REST hoàn chỉnh**: tạo tài khoản, chuyển khoản, tra cứu số dư, lịch sử giao dịch.
- Hiểu và dùng đúng **`@Transactional` + ACID** trong thực tế.
- Biết tổ chức code nhiều lớp: Controller → Service → Repository.

## Lý thuyết tối thiểu
- **Spring Core:** Dependency Injection (DI), IoC, `@Component`, `@Service`, `@Repository`, `@Autowired`, configuration.
- **Spring MVC REST:** `@RestController`, `@GetMapping`, `@PostMapping`, `@RequestBody`, `@PathVariable`, `@RequestParam`, response JSON.
- **Bean Validation:** `@NotNull`, `@Positive`, `@Size`, `@DecimalMin`, `@Valid` — validate dữ liệu client.
- **Spring Data JPA:** Entity, `@Repository interface extends JpaRepository`, `@Id`, `@Column`, `@CreatedDate`.
- **Transaction:** `@Transactional`, gọi từ Service (không gọi từ repository), propagation, rollback khi exception.
- **exception Handler:** `@RestControllerAdvice` + `@ExceptionHandler` trả JSON lỗi chuẩn.
- **Lombok** (giảm code getter/setter) — nếu team dùng.
- **Maven/Gradle** đọc được `pom.xml`/`build.gradle`.
- Kiểm tra bằng Postman hoặc cURL.

## Bài tập nghiệp vụ (làm tất cả — gộp thành 1 dự án `bank-api`)
1. **CRUD tài khoản:** `POST /api/account`, `GET /api/account/{id}`, `GET /api/account` (phân trang). Validate số tài khoản, CCCD.
2. **Chuyển khoản nội bộ (trái tim của nghiệp vụ):**
   - `POST /api/account/{from}/transfers` với `{ to, amount, note }`.
   - Trong 1 `@Transactional`: trừ `from`, cộng `to`, chèn bảng transaction — **nếu lỗi bất kỳ chỗ nào → rollback toàn bộ**.
   - Trả về mã giao dịch + trạng thái.
3. **Phân trang + lọc lịch sử:** `GET /api/account/{id}/transactions?from=...&to=...&type=...&page=0&size=20`.
4. **Nạp/rút tiền:** API nạp tiền và rút tiền kèm giới hạn hạn mức (VD: 1 ngày tối đa 50 triệu theo quy định NHNN cho giao dịch online).
5. **Central exception handler:** Mọi lỗi trả về `{ "code": "EBAL-001", "message": "Số dư không đủ" }` — ko trả raw exception.
6. **Tự viết ít nhất 5 unit test** cho Service chuyển tiền (JUnit) — trường hợp thành công, số dư không đủ, tài khoản không tồn tại, số tiền không hợp lệ.

## Ví dụ nghiệp vụ thực tế
- API chuyển khoản bạn xây chính là hình ảnh thu nhỏ của **core service chuyển tiền** của ngân hàng: validate → khóa tài khoản → trừ → cộng → ghi log → trả kết quả. `@Transactional` đảm bảo 4 bước cùng thành công hoặc cùng không.
- Giới hạn hạn mức giao dịch online (bài 4) được NHNN quy định (Thông tư về hoạt động thanh toán) — ngân hàng kiểm soát đúng luật.
- Chuẩn JSON lỗi dạng mã lỗi chuẩn hóa là cách cổng thanh toán thực tế trả về cho merchant để tự động xử lý.

## Checklist đạt chuẩn
- [ ] Tự xây được `bank-api` hoàn chỉnh chạy trên 8080, gọi bằng Postman.
- [ ] Giải thích được: vì sao `@Transactional` phải đặt ở Service, khi nào rollback xảy ra.
- [ ] Viết được 5 test không bị đỏ.
- [ ] Handle exception trả JSON đẹp không để stacktrace lộ ra.

---

# PHASE 9 — SECURITY + PRODUCTION + ISO 8583 (Tuần 61–72)

## Mục tiêu
- Bảo vệ API bằng **Spring Security + JWT**; mã hóa dữ liệu nhạy cảm.
- Hiểu **chuẩn PCI-DSS** (bảo mật thẻ) và giao dịch **ISO 8583** chữ ký số.
- Hiểu phòng chống tấn công web cơ bản (SQLi, XSS, IDOR, brute force).

## Lý thuyết tối thiết
- Spring Security: filter chain, `UserDetailsService`, password encoder (`BCrypt`), các `authority`.
- **JWT:** cấu trúc header.payload.signature; khi nào dùng access token/refresh token; **tuyệt đối không để card number/PAN vào token hay log**.
- Mã hóa đối xứng **AES** (mã hóa dữ liệu: PAN, số tài khoản nhạy cảm), bất đối xứng **RSA** (giải mã bằng private key), **HMAC** (verify chữ ký message giữa gateway - merchant / giữa các ngân hàng).
- **ISO 8583 thực chiến:** đóng gói message, MAC (Message Authentication Code) bằng DES/HMAC trên các trường — bảo đảm không bị sửa đổi giữa đường.
- **PCI-DSS:** không lưu CVV/PIN/mã từ track; mã hóa PAN khi lưu; log không chứa PAN đầy đủ (chỉ 4 số cuối).
- OWASP Top 10 sơ lược: SQLi, XSS, IDOR, Broken Auth.

## Bài tập nghiệp vụ (làm tất cả)
1. **Đăng ký/đăng nhập JWT:** Sửa `bank-api` Phase 8: `/api/auth/register`, `/api/auth/login` trả access token. Yêu cầu token khi gọi các API giao dịch.
2. **Phân quyền:** `ROLE_CUSTOMER` chỉ xem tài khoản của mình; `ROLE_ADMIN` xem được tất cả. Minh họa **IDOR**: hai khách không truy cập được tài khoản của nhau.
3. **Mã hóa một số dữ liệu:** Chọn 1 cột nhạy cảm (VD: số tài khoản nhạy cảm hoặc giả lập PAN) dùng **AES** mã hóa trước khi lưu và giải mã khi đọc (dùng `TextFieldConverter` trong JPA).
4. **Chữ ký HMAC cho webhook:** Xây endpoint nhận callback từ "merchant": mọi body đi kèm header `X-Signature = HMAC(body, secret)`. Server tính lại và so sánh chữ ký trước khi xử lý — **chống giả mạo request**.
5. **ISO 8583 giả lập có MAC:** Mô phỏng host ATM: đóng gói message, tính MAC, gửi/nhận, trả response. (Nếu có thời gian: dùng thư viện `j8583` nổi tiếng.)
6. **Audit log:** Ghi log mọi giao dịch + ai/xem/làm gì/lúc nào; **che số thẻ** (chỉ hiện 4 số cuối).

## Ví dụ nghiệp vụ thực tế
- Chuẩn **PCI-DSS** là điều kiện bắt buộc mọi tổ chức xử lý thẻ (Merchant, Payment Gateway) phải tuân thủ — nắm được nó là lợi thế khi phỏng vấn ngân hàng/thẻ.
- Khi merchant (shop) gọi API cổng thanh toán, giao dịch được **ky sign (HMAC/MAC)** để cổng xác định đúng người gửi và dữ liệu không bị sửa — đúng bài 4.
- MAC trong ISO 8583 chính là lý do bản tin ATM không thể bị hack dọc đường (kẻ gian sửa số tiền) — bài 5 mô phỏng chính xác cơ chế này.

## Checklist đạt chuẩn
- [ ] API toàn bộ được bảo vệ bằng JWT, phân quyền đúng.
- [ ] Mã hóa/giải mã được một trường nhạy cảm bằng AES.
- [ ] Hiểu và giải thích được import của MAC/HMAC trong giao dịch thẻ.
- [ ] Trả lời được: "Tại sao không được lưu CVV?" (PCI-DSS).

---

# PHASE 10 — MỞ RỘNG: REDIS, KAFKA, DOCKER (Tuần 73–80)

## Mục tiêu
- Dùng **Redis** cho cache + khóa phân tán (distributed lock).
- Dùng **Kafka/RabbitMQ** cho event-driven (thông báo giao dịch, bù trừ, đồng bộ microservice).
- Đóng gói API vào **Docker** container.

## Lý thuyết tối thiểu
- Redis: lưu cache (`get`/`set`, TTL), danh sách, key đơn giản; **khóa phân tán** (SET NX EX / Redisson).
- Kafka: topic, producer, consumer, consumer group; ví dụ event `TRANSACTION_CREATED`.
- Docker: `Dockerfile`, `docker run`, `docker compose` (chạy Postgres + Redis + app).

## Bài tập nghiệp vụ (làm tất cả)
1. **Cache số dư:** Lưu số dư vào Redis TTL 30 giây để đọc nhanh. Hiểu nhược điểm → sau khi chuyển tiền phải **invalidate cache**.
2. **Distributed lock khi chuyển khoản:** Khi có 2 instance app (tải) cùng xử lý, dùng Redis lock để tránh 2 giao dịch đồng thời trên 1 tài khoản.
3. **Sự kiện giao dịch:** Khi `@Transactional` commit thành công, publish sự kiện `transaction.completed` lên Kafka; một consumer gửi email/SMS thông báo, một consumer đẩy vào bảng đối soát.
4. **Chạy 3 container:** docker-compose với `db` (Postgres), `redis`, `app` (bank-api) — truy cập được từ máy.

## Ví dụ nghiệp vụ thực tế
- Các ngân hàng hiện đại (VD số hóa) chạy **microservice + event-driven**: khi giao dịch xong, sự kiện lan tỏa (ghi nhận kế toán, đối soát, thông báo, CRIF dư nợ...).
- Redis dùng cho **ví bù trừ nhanh**, lưu OTP, chống spam (rate limit) — không phải lúc nào cũng đọc DB lớn ngay.

## Checklist đạt chuẩn
- [ ] Chạy được app trong Docker, gọi được API.
- [ ] Cache số dư bằng Redis và invalidate sau giao dịch.
- [ ] Pub/sub một sự kiện transaction qua Kafka/Queue.

---

# PHASE 11 — CAPSTONE: MINI PAYMENT GATEWAY (Tuần 81–90)

> Dự án cuối để bỏ vào CV và portfolio. Mục tiêu: ghép toàn bộ kiến thức + tạo đề tài để trả lời phỏng vấn.

## Đề tài
Xây **mini payment gateway** phục vụ nhu cầu bán hàng online (mô phỏng Napas/VNPay).

## Phạm vi tối thiểu
1. **Merchant đăng ký** tích hợp: mỗi merchant có `merchantId` + `secretKey` (dùng HMAC ký request).
2. **Checkout:** Khách tạo đơn thanh toán → GET 1 trang nhập thẻ (demo) → gửi request `POST /api/v1/payments` gửi kèm `X-Webhook-URL`.
3. **Xử lý thanh toán:** Giả lập gọi "ngân hàng phát hành" (API ảo): trừ tiền → ghi giao dịch → đổi trạng thái theo chu kỳ `PENDING → PROCESSING → SUCCESS/FAILED → REVERSED`.
4. **Webhook + callback:** Khi trạng thái đổi, gọi webhook đến merchant **kèm HMAC chữ ký** để merchant verify.
5. **Tra soát/danh mục:** Merchant tra cứu history, đối soát theo mã giao dịch; admin xem dashboard tổng doanh thu.
6. **Bảo mật:** JWT cho merchant, mã hóa PAN, log che thẻ, rate limit chống brute-force đăng nhập.

## Kỹ thuật sử dụng (tổng kết toàn bộ lộ trình)
- Java 17+ / Spring Boot / JPA / PostgreSQL
- `@Transactional` ACID, Redis cache + lock, Kafka event
- Security: JWT + BCrypt + AES + HMAC, ISO-like signing
- Docker Compose, unit test, README chuẩn để tuyển dụng đọc được

## Checklist hoàn thành
- [ ] Demo 1 clipboard đầy đủ: merchant tạo link thanh toán → khách trả tiền → merchant nhận webhook thành công → trong DB mọi bảng khớp.
- [ ] Không còn `double` cho tiền ở bất kỳ đâu.
- [ ] Nhược điểm/giới hạn hệ thống — nêu rõ (bạn ĐÃ hiểu mình đã và chưa làm gì).

---

## LỘ TRÌNH HẸN HÒ VỚI PHỎNG VẤN

Khi apply backend ngân hàng/payment, các câu hỏi kinh điển mà bạn **nên trả lời được** ngay sau lộ trình:

1. Vì sao không dùng `double` cho tiền? → **BigDecimal** (Phase 4).
2. Giải thích ACID và ví dụ chuyển khoản. → Phase 5, 8.
3. Vì sao `@Transactional` đặt ở Service? rollback khi nào? → Phase 8.
4. Làm thế nào tránh 2 giao dịch cùng trừ tiền 1 tài khoản? → Phase 6 (lock) + Phase 10 (distributed lock).
5. Khi 2 người chuyển cho nhau cùng lúc, tránh deadlock thế nào? → Phase 6.
6. PCI-DSS cấm lưu gì? → CVV/PIN/duy trì track data; phase 9.
7. Tại sao phải dùng HMAC/MAC trong tin nhắn giao dịch? → Phase 9.
8. Sự khác biệt ISO 8583 vs REST API? → Phase 7, 9.
9. Làm sao đối soát khi hệ thống gửi-nhận mạng bị lỗi giữa chừng? → Phase 5 (transaction) + khái niệm **đối soát, hoàn trả, tra soát** (đọc thêm).
10. Bạn đã xử lý số dư âm chưa? Kiểm soát như thế nào? → `CHECK(balance>=0)` + khóa (Phase 5, 6).

## TÀI LIỆU THAM KHẢO GỢI Ý
- Java: **Head First Java** (bản tiếng Việt cũng được) hoặc Mooc **Java Programming** (University of Helsinki) miễn phí.
- OOP + Design Pattern: **Head First Design Patterns**.
- SQL: **SQLBolt** (miễn phí) + sách MySQL/PostgreSQL cơ bản.
- Spring Boot: **Spring Quickstart Guide** và **Spring Boot Reference** (đọc phần cần dùng, không đọc hết).
- ISO 8583: tài liệu thư viện **j8583** (GitHub) — đọc để hiểu thực tế.
- Tiếng Việt cập nhật quy định NHNN/TCTD làm ngân hàng: theo dõi trang NHNN (sbv.gov.vn) mục văn bản — giúp hiểu rào cản pháp lý trong nghiệp vụ.

## GHI CHÚ ĐỘNG LỰC
- Ngân hàng/payment ưu cái chữ "chính xác, an toàn, tuân thủ" hơn là "phần mềm nhanh". Mọi bài tập trên đều hướng theo 3 giá trị đó.
- Kiến thức Phase 8–9 mới là "chạm" lượng việc thực tế. Đừng nản nếu giai đoạn đầu thấy xa vời.
- Mỗi phase xong, đẩy code lên GitHub cá nhân với README rõ — CV nhà tuyển dụng sẽ đọc.

---

# CẤU TRÚC REPO & KẾ HOẠCH TÁCH MODULE

## Hiện tại

```
Study-Java/
├── README.md                    ← giới thiệu dự án, tiến độ, quyết định thiết kế
├── LICENSE                      ← MIT
├── docs/roadmap.md              ← file này
├── pom.xml                      ← Maven, layout chuẩn src/main/java
├── mvnw · mvnw.cmd · .mvn/      ← Maven Wrapper (pin 3.9.16)
├── .github/workflows/maven.yml  ← CI: build + 63 test
├── .gitignore
├── src/main/java/               ← ứng dụng console mô phỏng ngân hàng (27 file)
└── src/test/java/               ← 63 unit test
```

## Kế hoạch: tách module theo phase

Mỗi phase sẽ thành một Maven module riêng, để khi phỏng vấn có thể chỉ vào từng thư mục và
nói *"mỗi giai đoạn một module, code thật, test thật"*.

**Quyết định còn để ngỏ:** nhiều module trong **một** `pom.xml` (multi-module), hay **nhiều
project Maven độc lập**?

| | Multi-module (1 pom) | Nhiều project độc lập |
|---|---|---|
| Ưu | Một lệnh `./mvnw test` chạy hết · CI đơn giản | Mỗi phase chạy độc lập, giống repo thật hơn |
| Nhược | Các module lớn dần sẽ phụ thuộc lẫn nhau | Phải chạy `mvn` riêng từng thư mục |

→ **Ưu tiên multi-module** khi bắt đầu tách. Sẽ quyết định cụ thể ở Phase 4.

Cấu trúc dự kiến:

```
Study-Java/
├── pom.xml                      ← pom cha, khai báo các module
├── 01-core-basics/              ← Phase 1: ATM, lãi suất, Luhn
├── 02-oop-banking/              ← Phase 2: Account, TransferHandler, Factory
├── 03-collections/              ← Phase 3: ứng dụng console hiện tại
├── 04-money-bigdecimal/         ← Phase 4: sao kê CSV, phí chuyển khoản
├── 05-sql-jdbc/                 ← Phase 5: schema, transfer transaction, JDBC
├── 06-concurrency/              ← Phase 6: 1000 thread, deadlock
├── 07-http-iso8583/             ← Phase 7: REST quiz, mock POS, TCP ATM
├── 08-spring-boot-api/          ← Phase 8: bank-api, @Transactional
├── 09-security/                 ← Phase 9: JWT, AES, HMAC, ISO 8583 MAC
├── 10-advanced/                 ← Phase 10: redis-cache, kafka-event, docker
└── capstone-payment-gateway/    ← Phase 11: mini payment gateway
```

## Quy ước bên trong mỗi module

- Mỗi bài tập = 1 class hoặc 1 thư mục con có tên rõ nghĩa.
- Luôn có `README.md` tóm tắt: bài toán nghiệp vụ + cách chạy.
- Từ `01` dùng `BigDecimal` cho tiền, **không bao giờ** dùng `double` cho số tiền (mể cả
  Phase 1 cũng vậy — đúng mindset ngân hàng).
- Phase 5 trở đi: mỗi project có schema SQL + bước chạy (docker hoặc script) để người khác
  chạy được.
- Commit mỗi lần xong 1 bài, message tiếng Anh ngắn và **nói rõ vì sao**, không chỉ nói làm gì.

---

# HAI BÀI HỌC RÚT RA TỪ CHÍNH PROJECT NÀY

Áp dụng cho các phase sau:

1. **Đừng tin tín hiệu "thành công" mặc định.** Khi chuyển project này sang Maven lần đầu,
   CI báo xanh trong khi **biên dịch 0 file** — vì code nằm sai chỗ so với layout Maven mặc
   định. Xanh vì không làm gì cả. Luôn kiểm tra log có dòng `Compiling N source files` với
   `N > 0`.

2. **Test xanh không chứng minh test có tác dụng.** Đã chạy mutation test — cố tình phá code
   rồi xem test có đỏ không. Chi tiết ở [`../README.md`](../README.md).

3. **Đặt tên là công cụ bắt lỗi, không phải làm cho đẹp.** Một tên quá chung dễ mang nhiều
   nghĩa: `balance` vừa là số dư hiện tại vừa là số dư cuối kỳ → báo cáo sai mà nhìn rất hợp
   lý. Đổi thành `closingBalance` là **bắt buộc**, không phải tuỳ thích.

Cùng một nguyên tắc chung: **tín hiệu mặc định không kiểm chứng được điều nó cần chứng minh.**