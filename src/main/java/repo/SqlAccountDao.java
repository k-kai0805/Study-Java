package repo;

import config.DatabaseConnection;
import model.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlAccountDao implements AccountDao {

    private static final String FIND_BY_ID_SQL = """
            SELECT id, customer_id, account_number, balance, status, opened_at
            FROM account WHERE id = ?
            """;

    private static final String FIND_BY_NUMBER_SQL = """
            SELECT id, customer_id, account_number, balance, status, opened_at
            FROM account WHERE account_number = ?
            """;

    private static final String FIND_BY_NUMBER_FOR_UPDATE_SQL = """
            SELECT id, customer_id, account_number, balance, status, opened_at
            FROM account WHERE account_number = ? FOR UPDATE
            """;

    private static final String FIND_ALL_SQL = """
            SELECT
            ac.id,
            ac.customer_id,
            ct.full_name,
            ac.account_number,
            ac.balance,
            ac.status,
            ac.opened_at
            FROM account ac
            INNER JOIN customer ct
            ON ac.customer_id = ct.id
            ORDER BY ac.account_number
            """;

    // H2-specific MERGE INTO with KEY clause for upsert
    private static final String SAVE_SQL = """
            MERGE INTO account (customer_id, account_number, owner_name, balance, status, opened_at)
            KEY (account_number)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_BALANCE_SQL = """
            UPDATE account SET balance = ? WHERE account_number = ?
            """;

    @Override
    public Optional<Account> findById(Long id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find account by id: " + id, e);
        }
    }

    @Override
    public Optional<Account> findByNumber(String accountNumber) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_NUMBER_SQL)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find account by number: " + accountNumber, e);
        }
    }

    @Override
    public Optional<Account> findByNumber(Connection conn, String accountNumber) {
        try (PreparedStatement stmt = conn.prepareStatement(FIND_BY_NUMBER_SQL)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find account by number: " + accountNumber, e);
        }
    }

    @Override
    public Optional<Account> findByNumberForUpdate(Connection conn, String accountNumber) {
        try (PreparedStatement stmt = conn.prepareStatement(FIND_BY_NUMBER_FOR_UPDATE_SQL)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find account for update: " + accountNumber, e);
        }
    }

    @Override
    public List<Account> findAll() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            List<Account> accounts = new ArrayList<>();
            while (rs.next()) {
                accounts.add(mapRow(rs));
            }
            return accounts;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all accounts", e);
        }
    }

    @Override
    public void save(Connection conn, Account account) {
        try (PreparedStatement stmt = conn.prepareStatement(SAVE_SQL)) {
            stmt.setObject(1, account.getCustomerId());
            stmt.setString(2, account.getAccountNumber());
            stmt.setBigDecimal(4, account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO);
            stmt.setString(5, account.getStatus() != null ? account.getStatus() : "ACTIVE");
            stmt.setTimestamp(6, account.getOpenedAt() != null ? Timestamp.valueOf(account.getOpenedAt()) : Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save account: " + account.getAccountNumber(), e);
        }
    }

    @Override
    public void updateBalance(Connection conn, String accountNumber, BigDecimal newBalance) {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_BALANCE_SQL)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setString(2, accountNumber);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Account not found: " + accountNumber);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update balance for account: " + accountNumber, e);
        }
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account(
                rs.getLong("id"),
                rs.getObject("customer_id", Long.class),
                rs.getString("account_number"),
                rs.getString("full_name"),
                rs.getBigDecimal("balance"),
                rs.getString("status"),
                rs.getTimestamp("opened_at") != null ? rs.getTimestamp("opened_at").toLocalDateTime() : null,
                new ArrayList<>()
        );
    }
}