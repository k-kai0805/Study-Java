-- =============================================================
--  1. KHACH HANG
--  Quan he 1-N voi account: mot khach co nhieu tai khoan
-- =============================================================
CREATE TABLE customer (
    id         BIGSERIAL    PRIMARY KEY,
    full_name  VARCHAR(100) NOT NULL,
    cccd       VARCHAR(12)  NOT NULL UNIQUE,   -- CCCD la dinh danh duy nhat
    phone      VARCHAR(20),
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- =============================================================
--  2. TAI KHOAN
--  balance luon >= 0: rang buoc o TANG DU LIEU, khong phai trong Java
-- =============================================================
CREATE TABLE account (
    id             BIGSERIAL    PRIMARY KEY,
    customer_id    BIGINT       NOT NULL REFERENCES customer(id),
    account_number VARCHAR(20)  NOT NULL UNIQUE,
    balance        NUMERIC(15,2) NOT NULL DEFAULT 0,
    status         VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    opened_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0)
);

CREATE INDEX idx_account_customer ON account (customer_id);

-- =============================================================
--  3. GIAO DICH
--  KHONG co cot afterBalance: so du nam o bang lich su, khong lap lai o day
--  reference_no UNIQUE: chong trung o TANG DU LIEU
-- =============================================================
CREATE TABLE transaction (
    id           BIGSERIAL     PRIMARY KEY,
    account_id   BIGINT        NOT NULL REFERENCES account(id),
    reference_no VARCHAR(50)   NOT NULL UNIQUE,  -- <-- lop bao ve cuoi cung
    amount       NUMERIC(15,2) NOT NULL,
    type         VARCHAR(20)   NOT NULL,
    created_at   TIMESTAMPTZ   NOT NULL,
    description  VARCHAR(255),

    CONSTRAINT chk_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_type CHECK (type IN ('DEPOSIT','WITHDRAW','TRANSFER_OUT','TRANSFER_IN'))
);

CREATE INDEX idx_transaction_account_created ON transaction (account_id, created_at);

-- =============================================================
--  4. LICH SU SO DU
--  transaction_id NULL = dong so du khi MO tai khoan (chua co giao dich nao)
--  id BIGSERIAL = khoa pha thoa ho khi 2 giao dich trung thoi gian
-- =============================================================
CREATE TABLE account_balance_history (
    id             BIGSERIAL     PRIMARY KEY,
    account_id     BIGINT        NOT NULL REFERENCES account(id),
    transaction_id BIGINT        REFERENCES transaction(id),
    balance        NUMERIC(15,2) NOT NULL,
    as_of          TIMESTAMPTZ   NOT NULL,

    CONSTRAINT chk_history_non_negative CHECK (balance >= 0)
);

-- Tru van "so du tai thoi diem T"
CREATE INDEX idx_history_account_asof ON account_balance_history (account_id, as_of);