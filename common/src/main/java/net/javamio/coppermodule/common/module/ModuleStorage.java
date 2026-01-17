package net.javamio.coppermodule.common.module;

import net.javamio.coppermodule.common.module.exception.StorageException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public interface ModuleStorage {

    void saveModuleState(@NotNull String moduleIdentifier, @NotNull ModuleState state);

    @NotNull Optional<ModuleState> loadModuleState(@NotNull String moduleIdentifier);

    @NotNull Map<String, ModuleState> loadAllModuleStates();

    void deleteModuleState(@NotNull String moduleIdentifier);

    void initialize() throws StorageException;

    void close() throws StorageException;

    boolean isConnected();
}
