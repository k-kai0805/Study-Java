package repo;

import config.DatabaseConnection;
import model.AccountBalanceHistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlAccountBalanceHistoryDao implements AccountBalanceHistoryDao {

    private static final String INSERT_SQL = """
            INSERT INTO account_balance_history (account_id, transaction_id, balance, as_of)
            VALUES (?, ?, ?, ?)
            """;

    private static final String FIND_LATEST_BY_ACCOUNT_ID_SQL = """
            SELECT id, account_id, transaction_id, balance, as_of
            FROM account_balance_history
            WHERE account_id = ?
            ORDER BY as_of DESC, id DESC
            LIMIT 1
            """;

    private static final String FIND_BY_ACCOUNT_ID_SQL = """
            SELECT id, account_id, transaction_id, balance, as_of
            FROM account_balance_history
            WHERE account_id = ?
            ORDER BY as_of DESC, id DESC
            """;

    private static final String FIND_BY_ACCOUNT_ID_AND_DATE_RANGE_SQL = """
            SELECT id, account_id, transaction_id, balance, as_of
            FROM account_balance_history
            WHERE account_id = ?
              AND as_of >= ?
              AND as_of <= ?
            ORDER BY as_of DESC, id DESC
            """;

    private static final String FIND_ALL_BY_DATE_SQL = """
            SELECT id, account_id, transaction_id, balance, as_of
            FROM account_balance_history
            WHERE as_of::date = ?
            ORDER BY account_id
            """;

    private static final String FIND_LATEST_BY_ACCOUNT_SQL = """
            SELECT id, account_id, transaction_id, balance, as_of
            FROM account_balance_history
            WHERE account_id = ?
            ORDER BY as_of DESC
            LIMIT 1
            """;

    @Override
    public void save(Connection conn, AccountBalanceHistory history) {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setLong(1, history.accountId());
            if (history.transactionId() != null) {
                stmt.setLong(2, history.transactionId());
            } else {
                stmt.setNull(2, Types.BIGINT);
            }
            stmt.setBigDecimal(3, history.balance());
            stmt.setTimestamp(4, Timestamp.valueOf(history.asOf()));
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save balance history for account: " + history.accountId(), e);
        }
    }

    @Override
    public List<AccountBalanceHistory> findByAccountNumber(String accountNumber) {
        // Need to get accountId from accountNumber first
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT id FROM account WHERE account_number = ?")) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return List.of();
                }
                Long accountId = rs.getLong("id");
                return findByAccountId(accountId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find balance history for account: " + accountNumber, e);
        }
    }

    private List<AccountBalanceHistory> findByAccountId(Long accountId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT id, account_id, transaction_id, balance, as_of FROM account_balance_history WHERE account_id = ? ORDER BY as_of DESC, id DESC")) {
            stmt.setLong(1, accountId);

            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSetToHistory(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find balance history for accountId: " + accountId, e);
        }
    }

    @Override
    public List<AccountBalanceHistory> findByAccountNumberAndDateRange(String accountNumber, java.time.LocalDate fromDate, java.time.LocalDate toDate) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT id FROM account WHERE account_number = ?")) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return List.of();
                }
                Long accountId = rs.getLong("id");
                return findByAccountIdAndDateRange(accountId, fromDate, toDate);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find balance history for account: " + accountNumber, e);
        }
    }

    private List<AccountBalanceHistory> findByAccountIdAndDateRange(Long accountId, java.time.LocalDate fromDate, java.time.LocalDate toDate) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT id, account_id, transaction_id, balance, as_of FROM account_balance_history WHERE account_id = ? AND as_of >= ? AND as_of <= ? ORDER BY as_of DESC, id DESC")) {

            stmt.setLong(1, accountId);
            stmt.setTimestamp(2, Timestamp.valueOf(fromDate.atStartOfDay()));
            stmt.setTimestamp(3, Timestamp.valueOf(toDate.atTime(23, 59, 59)));

            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSetToHistory(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find balance history for accountId: " + accountId, e);
        }
    }

    @Override
    public List<AccountBalanceHistory> findAllByDate(java.time.LocalDate date) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_ALL_BY_DATE_SQL)) {

            stmt.setTimestamp(1, Timestamp.valueOf(date.atStartOfDay()));

            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSetToHistory(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all balance history for date: " + date, e);
        }
    }

    @Override
    public Optional<AccountBalanceHistory> findLatestByAccountNumber(String accountNumber) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT id FROM account WHERE account_number = ?")) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Long accountId = rs.getLong("id");
                return findLatestByAccountId(accountId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find latest balance history for account: " + accountNumber, e);
        }
    }

    private Optional<AccountBalanceHistory> findLatestByAccountId(Long accountId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT id, account_id, transaction_id, balance, as_of FROM account_balance_history WHERE account_id = ? ORDER BY as_of DESC, id DESC LIMIT 1")) {

            stmt.setLong(1, accountId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToHistory(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find latest balance history for accountId: " + accountId, e);
        }
    }

    private List<AccountBalanceHistory> mapResultSetToHistory(ResultSet rs) throws SQLException {
        List<AccountBalanceHistory> historyList = new ArrayList<>();
        while (rs.next()) {
            historyList.add(mapRowToHistory(rs));
        }
        return historyList;
    }

    private AccountBalanceHistory mapRowToHistory(ResultSet rs) throws SQLException {
        return new AccountBalanceHistory(
                rs.getLong("id"),
                rs.getLong("account_id"),
                rs.getObject("transaction_id", Long.class),
                rs.getBigDecimal("balance"),
                rs.getTimestamp("as_of") != null ? rs.getTimestamp("as_of").toLocalDateTime() : null
        );
    }
}