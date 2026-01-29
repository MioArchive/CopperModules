package net.javamio.coppermodule.common.module.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.javamio.coppermodule.common.module.ModuleState;
import net.javamio.coppermodule.common.module.exception.StorageException;
import net.javamio.coppermodule.common.module.storage.ModuleStorage;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class MySqlStorage implements ModuleStorage {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String tableName;

    private Connection connection = null;
    private final Logger logger = Logger.getLogger(MySqlStorage.class.getName());

    @Override
    public void saveModuleState(@NotNull String moduleIdentifier, @NotNull ModuleState state) {
        Thread.ofVirtual().start(() -> {
            final String sql = String.format("INSERT INTO `%s` (module_identifier, module_state) VALUES (?, ?) ON DUPLICATE KEY UPDATE module_state = ?", this.tableName);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, moduleIdentifier);
                statement.setString(2, state.name());
                statement.setString(3, state.name());
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.warning("Failed to save module state for '" + moduleIdentifier + "': " + e.getMessage());
            }
        });
    }

    @Override
    @SneakyThrows
    public @NotNull Optional<ModuleState> loadModuleState(@NotNull String moduleIdentifier) {
        final String sql = String.format("SELECT module_state FROM `%s` WHERE module_identifier = ?", this.tableName);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, moduleIdentifier);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(ModuleState.valueOf(resultSet.getString("module_state")));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    @SneakyThrows
    public @NotNull Map<String, ModuleState> loadAllModuleStates() {
        final @NotNull Map<String, ModuleState> states = new HashMap<>();
        final String sql = String.format("SELECT module_identifier, module_state FROM `%s`", this.tableName);

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                states.put(resultSet.getString("module_identifier"), ModuleState.valueOf(resultSet.getString("module_state")));
            }
        }
        return states;
    }

    @Override
    public void deleteModuleState(@NotNull String moduleIdentifier) {
        Thread.ofVirtual().start(() -> {
            final String sql = String.format("DELETE FROM `%s` WHERE module_identifier = ?", this.tableName);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, moduleIdentifier);
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.warning("Failed to delete module state for '" + moduleIdentifier + "': " + e.getMessage());
            }
        });
    }

    @Override
    public void initialize() throws SQLException {
        final String url = "jdbc:mysql://" + host + ":" + port + "/" + database;

        final Properties properties = new Properties();
        properties.put("user", username);
        properties.put("password", password);
        properties.put("autoReconnect", "true");
        properties.put("useSSL", "false");

        connection = DriverManager.getConnection(url, properties);
        createTable();
    }

    @Override
    public void close() throws StorageException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new StorageException("Failed to close MySQL connection", e);
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    private void createTable() throws SQLException {
        final String sql = String.format(
                "CREATE TABLE IF NOT EXISTS `%s` (" +
                        "`module_identifier` VARCHAR(255) PRIMARY KEY," +
                        "`module_state` VARCHAR(50) NOT NULL," +
                        "`last_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
                this.tableName
        );

        try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }
}