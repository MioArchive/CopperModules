package net.javamio.coppermodule.common.module.storage;

import net.javamio.coppermodule.common.module.ModuleState;
import net.javamio.coppermodule.common.module.exception.StorageException;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public interface ModuleStorage {

    void saveModuleState(@NotNull String moduleIdentifier, @NotNull ModuleState state);

    @NotNull Optional<ModuleState> loadModuleState(@NotNull String moduleIdentifier);

    @NotNull Map<String, ModuleState> loadAllModuleStates();

    void deleteModuleState(@NotNull String moduleIdentifier);

    void initialize() throws StorageException, SQLException;

    void close() throws StorageException;

    boolean isConnected();
}
