-- =============================================================
--  SEED DATA: 8 khach, 9 tai khoan, 9 dong lich su so du
--  Chay SAU khi da co schema.sql
-- =============================================================

-- =============================================================
--  1. 8 KHACH HANG
--  CCCD va so dien thoai la DU LIEU GIA LAP.
--  Khong dung so co that trong thuc te.
-- =============================================================
INSERT INTO customer (full_name, cccd, phone) VALUES
  ('Nguyen Quoc Khanh', '000000000001', '0900000001'),
  ('Tran Van A',         '000000000002', '0900000002'),
  ('Le Thi B',           '000000000003', '0900000003'),
  ('Pham Van C',         '000000000004', '0900000004'),
  ('Nguyen Thi D',       '000000000005', '0900000005'),
  ('Do Van E',           '000000000006', '0900000006'),
  ('Tran Quang F',       '000000000007', '0900000007'),
  ('Vu Thi G',           '000000000008', '0900000008');

-- =============================================================
--  2. 9 TAI KHOAN (1 khach co 2 tai khoan: ACC10001 + ACC10009)
--  JOIN bang CCCD de luon dung, khong phu thuoc vao BIGSERIAL id
-- =============================================================
INSERT INTO account (customer_id, account_number, balance)
SELECT c.id, v.acc, v.bal
FROM (VALUES
  ('000000000001', 'ACC10001', 1500000.00),
  ('000000000001', 'ACC10009', 2800000.00),   -- tai khoan thu 2 cua Khanh
  ('000000000002', 'ACC10002',  800000.00),
  ('000000000003', 'ACC10003', 2250000.00),
  ('000000000004', 'ACC10004',  350000.00),
  ('000000000005', 'ACC10005', 5000000.00),
  ('000000000006', 'ACC10006', 1250000.00),
  ('000000000007', 'ACC10007',  900000.00),
  ('000000000008', 'ACC10008', 4750000.00)
) AS v (cccd, acc, bal)
JOIN customer c ON c.cccd = v.cccd;

-- =============================================================
--  3. DONG LICH SU SO DU KHOI TAO (BAT BUOC)
--  transaction_id NULL = dong so du khi MO tai khoan (chua co GD nao)
--  as_of = opened_at
-- =============================================================
INSERT INTO account_balance_history (account_id, transaction_id, balance, as_of)
SELECT id, NULL, balance, opened_at FROM account;

-- =============================================================
--  XAC MINH (chay de kiem tra)
--  1. 8 khach, 9 tai khoan, 9 dong lich su
--  2. Quan he 1-N: Khanh co 2, cac nguoi khac moi 1
--  3. Tong so du = 19600000.00
--  4. Lich su ACC10002 co 1 dong balance 800000.00
-- =============================================================
-- SELECT count(*) FROM customer;          -- 8
-- SELECT count(*) FROM account;           -- 9
-- SELECT count(*) FROM account_balance_history; -- 9
-- SELECT c.full_name, count(a.id) AS so_tk
-- FROM customer c LEFT JOIN account a ON a.customer_id = c.id
-- GROUP BY c.full_name ORDER BY so_tk DESC;
-- SELECT sum(balance) FROM account;       -- 19600000.00
-- SELECT a.account_number, h.balance, h.as_of
-- FROM account_balance_history h
-- JOIN account a ON a.id = h.account_id
-- WHERE a.account_number = 'ACC10002'
-- ORDER BY h.as_of, h.id;