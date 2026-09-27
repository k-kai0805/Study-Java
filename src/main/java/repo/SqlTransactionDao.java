package repo;

import config.DatabaseConnection;
import model.Transaction;
import model.TransactionCriteria;
import model.TypeTransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlTransactionDao implements TransactionDao {

    private static final String INSERT_SQL = """
            INSERT INTO transaction (reference_no, account_id, amount, type, created_at, after_balance, description)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_REFERENCE_SQL = """
            SELECT t.reference_no, a.account_number, t.amount, t.type, t.created_at, t.after_balance, t.description
            FROM transaction t
            JOIN account a ON t.account_id = a.id
            WHERE t.reference_no = ?
            """;

    private static final String FIND_BY_ACCOUNT_SQL = """
            SELECT t.reference_no, a.account_number, t.amount, t.type, t.created_at, t.after_balance, t.description
            FROM transaction t
            JOIN account a ON t.account_id = a.id
            WHERE a.account_number = ?
            ORDER BY t.created_at DESC
            """;

    private static final String FIND_BY_ACCOUNT_WITH_CRITERIA_SQL = """
            SELECT t.reference_no, a.account_number, t.amount, t.type, t.created_at, t.after_balance, t.description
            FROM transaction t
            JOIN account a ON t.account_id = a.id
            WHERE a.account_number = ?
              AND (? IS NULL OR t.type = ?)
              AND (? IS NULL OR t.created_at >= ?)
              AND (? IS NULL OR t.created_at <= ?)
            ORDER BY t.created_at DESC
            """;

    private static final String FIND_ALL_WITH_CRITERIA_SQL = """
            SELECT t.reference_no, a.account_number, t.amount, t.type, t.created_at, t.after_balance, t.description
            FROM transaction t
            JOIN account a ON t.account_id = a.id
            WHERE (? IS NULL OR t.type = ?)
              AND (? IS NULL OR t.created_at >= ?)
              AND (? IS NULL OR t.created_at <= ?)
            ORDER BY t.created_at DESC
            """;

    @Override
    public void save(Connection conn, Transaction transaction) {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, transaction.referenceNo());
            stmt.setLong(2, getAccountId(conn, transaction.accountNumber()));
            stmt.setBigDecimal(3, transaction.amount());
            stmt.setString(4, transaction.type().name());
            stmt.setTimestamp(5, Timestamp.valueOf(transaction.timestamp()));
            stmt.setBigDecimal(6, transaction.afterBalance());
            stmt.setString(7, ""); // description
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save transaction: " + transaction.referenceNo(), e);
        }
    }

    @Override
    public Optional<Transaction> findByReferenceNo(Connection conn, String referenceNo) {
        try (PreparedStatement stmt = conn.prepareStatement(FIND_BY_REFERENCE_SQL)) {
            stmt.setString(1, referenceNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find transaction by reference no: " + referenceNo, e);
        }
    }

    @Override
    public Optional<Transaction> findByReferenceNo(String referenceNo) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_REFERENCE_SQL)) {
            stmt.setString(1, referenceNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find transaction by reference no: " + referenceNo, e);
        }
    }

    @Override
    public List<Transaction> findByAccountNumber(Connection conn, String accountNumber, TransactionCriteria criteria) {
        String sql = (criteria == null) ? FIND_BY_ACCOUNT_SQL : FIND_BY_ACCOUNT_WITH_CRITERIA_SQL;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            if (criteria != null) {
                setCriteriaParameters(stmt, criteria, 2);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find transactions for account: " + accountNumber, e);
        }
    }

    @Override
    public List<Transaction> findByAccountNumber(String accountNumber, TransactionCriteria criteria) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findByAccountNumber(conn, accountNumber, criteria);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find transactions for account: " + accountNumber, e);
        }
    }

    @Override
    public List<Transaction> findByAccountNumber(String accountNumber) {
        return findByAccountNumber(accountNumber, null);
    }

    @Override
    public List<Transaction> findAll(Connection conn, TransactionCriteria criteria) {
        String sql = (criteria == null) ?
                "SELECT t.reference_no, a.account_number, t.amount, t.type, t.created_at, t.after_balance, t.description FROM transaction t JOIN account a ON t.account_id = a.id ORDER BY t.created_at DESC" :
                FIND_ALL_WITH_CRITERIA_SQL;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (criteria != null) {
                setCriteriaParameters(stmt, criteria, 1);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all transactions", e);
        }
    }

    @Override
    public List<Transaction> findAll(TransactionCriteria criteria) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findAll(conn, criteria);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all transactions", e);
        }
    }

    private void setCriteriaParameters(PreparedStatement stmt, TransactionCriteria criteria, int startIndex) throws SQLException {
        int paramIndex = startIndex;

        // type
        if (criteria.typeTransaction() == null) {
            stmt.setNull(paramIndex++, Types.VARCHAR);
            stmt.setNull(paramIndex++, Types.VARCHAR);
        } else {
            String typeName = criteria.typeTransaction().name();
            stmt.setString(paramIndex++, typeName);
            stmt.setString(paramIndex++, typeName);
        }

        // fromDate
        if (criteria.fromDate() == null) {
            stmt.setNull(paramIndex++, Types.TIMESTAMP);
            stmt.setNull(paramIndex++, Types.TIMESTAMP);
        } else {
            Timestamp fromTs = Timestamp.valueOf(criteria.fromDate().atStartOfDay());
            stmt.setTimestamp(paramIndex++, fromTs);
            stmt.setTimestamp(paramIndex++, fromTs);
        }

        // toDate
        if (criteria.toDate() == null) {
            stmt.setNull(paramIndex++, Types.TIMESTAMP);
            stmt.setNull(paramIndex++, Types.TIMESTAMP);
        } else {
            Timestamp toTs = Timestamp.valueOf(criteria.toDate().atTime(23, 59, 59));
            stmt.setTimestamp(paramIndex++, toTs);
            stmt.setTimestamp(paramIndex++, toTs);
        }
    }

    private Long getAccountId(String accountNumber) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id FROM account WHERE account_number = ?")) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
                throw new RuntimeException("Account not found: " + accountNumber);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find account id: " + accountNumber, e);
        }
    }

    private Long getAccountId(Connection conn, String accountNumber) {
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM account WHERE account_number = ?")) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
                throw new RuntimeException("Account not found: " + accountNumber);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find account id: " + accountNumber, e);
        }
    }

    private List<Transaction> mapResultSet(ResultSet rs) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        while (rs.next()) {
            transactions.add(mapRow(rs));
        }
        return transactions;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getString("reference_no"),
                rs.getString("account_number"),
                rs.getBigDecimal("amount"),
                TypeTransaction.valueOf(rs.getString("type")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBigDecimal("after_balance")
        );
    }
}