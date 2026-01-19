/*
 * This file is part of CopperModules - https://github.com/MioArchive/CopperModules
 * Copyright (C) 2025 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.javamio.coppermodule.common.module;

import lombok.Getter;
import net.javamio.coppermodule.common.module.exception.ModuleException;
import net.javamio.coppermodule.common.module.storage.ModuleStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ModuleManager {

    private final @NotNull Map<String, Module> modules;

    @Getter
    private final @Nullable ModuleStorage storage;

    private final boolean persistStates;

    public ModuleManager(final @Nullable ModuleStorage storage, final boolean persistStates) {
        this.modules = new ConcurrentHashMap<>();
        this.storage = storage;
        this.persistStates = persistStates;
    }

    public ModuleManager() {
        this(null, false);
    }

    public void registerModule(@NotNull final Module module) throws ModuleException {
        if (this.modules.containsKey(module.getIdentifier())) {
            throw new ModuleException("Module with identifier '" + module.getIdentifier() + "' is already registered");
        }

        this.modules.put(module.getIdentifier(), module);
    }

    public void unregisterModule(@NotNull final String identifier) throws ModuleException {
        final @NotNull Module module = this.modules.get(identifier);
        if (module == null) {
            throw new ModuleException("Module with identifier '" + identifier + "' is not registered");
        }

        if (module.getState() != ModuleState.UNLOADED) {
            throw new ModuleException("Cannot unregister module '" + identifier + "' while it is not unloaded");
        }

        this.modules.remove(identifier);

        if (this.persistStates && this.storage != null) {
            this.storage.deleteModuleState(identifier);
        }
    }

    public @NotNull Optional<@NotNull Module> getModule(@NotNull final String identifier) {
        return Optional.ofNullable(this.modules.get(identifier));
    }

    public @NotNull Collection<@NotNull Module> getModules() {
        return Collections.unmodifiableCollection(this.modules.values());
    }

    public void loadModule(@NotNull final String identifier) throws ModuleException {
        final @NotNull Module module = this.getModuleOrThrow(identifier);

        if (module.getState() != ModuleState.UNLOADED) {
            throw new ModuleException("Module '" + identifier + "' is already loaded");
        }

        for (final @NotNull String dependency : module.getDependencies()) {
            final @NotNull Module dependencyModule = this.getModuleOrThrow(dependency);
            if (dependencyModule.getState() == ModuleState.UNLOADED) {
                this.loadModule(dependency);
            }
        }

        try {
            module.onLoad();
            module.setState(ModuleState.LOADED);
            this.saveState(module);
        } catch (final Exception exception) {
            module.setState(ModuleState.ERROR);
            this.saveState(module);
            throw new ModuleException("Failed to load module '" + identifier + "'", exception);
        }
    }

    public void enableModule(@NotNull final String identifier) throws ModuleException {
        final @NotNull Module module = this.getModuleOrThrow(identifier);

        if (module.getState() == ModuleState.ENABLED) {
            throw new ModuleException("Module '" + identifier + "' is already enabled");
        }

        if (module.getState() == ModuleState.UNLOADED) {
            this.loadModule(identifier);
        }

        for (final @NotNull String dependency : module.getDependencies()) {
            final @NotNull Module dependencyModule = this.getModuleOrThrow(dependency);
            if (dependencyModule.getState() != ModuleState.ENABLED) {
                throw new ModuleException("Cannot enable module '" + identifier + "' because dependency '" + dependency + "' is not enabled");
            }
        }

        try {
            module.onEnable();
            module.setState(ModuleState.ENABLED);
            this.saveState(module);

            for (final @NotNull SubModule subModule : module.getSubModules()) {
                this.enableModule(subModule.getIdentifier());
            }
        } catch (final Exception exception) {
            module.setState(ModuleState.ERROR);
            this.saveState(module);
            throw new ModuleException("Failed to enable module '" + identifier + "'", exception);
        }
    }

    public void disableModule(@NotNull final String identifier) throws ModuleException {
        final @NotNull Module module = this.getModuleOrThrow(identifier);

        if (module.getState() != ModuleState.ENABLED) {
            throw new ModuleException("Module '" + identifier + "' is not enabled");
        }

        final @NotNull List<@NotNull Module> dependentModules = this.modules.values().stream()
                .filter(m -> m.getDependencies().contains(identifier))
                .filter(m -> m.getState() == ModuleState.ENABLED)
                .toList();


        for (final @NotNull Module dependent : dependentModules) {
            this.disableModule(dependent.getIdentifier());
        }

        for (final @NotNull SubModule subModule : module.getSubModules()) {
            if (subModule.getState() == ModuleState.ENABLED) {
                this.disableModule(subModule.getIdentifier());
            }
        }

        try {
            module.onDisable();
            module.setState(ModuleState.DISABLED);
            this.saveState(module);
        } catch (final Exception exception) {
            module.setState(ModuleState.ERROR);
            this.saveState(module);
            throw new ModuleException("Failed to disable module '" + identifier + "'", exception);
        }
    }

    public void unloadModule(@NotNull final String identifier) throws ModuleException {
        final @NotNull Module module = this.getModuleOrThrow(identifier);

        if (module.getState() == ModuleState.ENABLED) {
            this.disableModule(identifier);
        }

        if (module.getState() == ModuleState.UNLOADED) {
            throw new ModuleException("Module '" + identifier + "' is already unloaded");
        }

        try {
            module.onUnload();
            module.setState(ModuleState.UNLOADED);
            this.saveState(module);
        } catch (final Exception exception) {
            module.setState(ModuleState.ERROR);
            this.saveState(module);
            throw new ModuleException("Failed to unload module '" + identifier + "'", exception);
        }
    }

    public void reloadModule(final @NotNull String identifier) throws ModuleException {
        final @NotNull Module module = this.getModuleOrThrow(identifier);
        final ModuleState previousState = module.getState();

        if (previousState == ModuleState.ENABLED) {
            this.disableModule(identifier);
        }

        if (module.getState() != ModuleState.UNLOADED) {
            this.unloadModule(identifier);
        }

        this.loadModule(identifier);

        if (previousState == ModuleState.ENABLED) {
            this.enableModule(identifier);
        }
    }

    public void restoreStates() throws ModuleException {
        if (!this.persistStates || this.storage == null) {
            return;
        }

        if (!this.storage.isConnected()) {
            throw new ModuleException("Storage is not connected");
        }

        final @NotNull Map<String, ModuleState> savedStates = this.storage.loadAllModuleStates();

        for (final Map.Entry<String, ModuleState> entry : savedStates.entrySet()) {
            final @NotNull String identifier = entry.getKey();
            final @NotNull ModuleState savedState = entry.getValue();

            if (!this.modules.containsKey(identifier)) {
                continue;
            }

            if (savedState == ModuleState.LOADED || savedState == ModuleState.ENABLED) {
                try {
                    this.loadModule(identifier);
                } catch (final ModuleException exception) {
                    System.err.println("Failed to restore load state for module '" + identifier + "': " + exception.getMessage());
                }
            }
        }

        for (final Map.Entry<String, ModuleState> entry : savedStates.entrySet()) {
            final String identifier = entry.getKey();
            final ModuleState savedState = entry.getValue();

            if (!this.modules.containsKey(identifier)) {
                continue;
            }

            if (savedState == ModuleState.ENABLED) {
                try {
                    this.enableModule(identifier);
                } catch (final ModuleException exception) {
                    System.err.println("Failed to restore enable state for module '" + identifier + "': " + exception.getMessage());
                }
            }
        }
    }

    private void saveState(final @NotNull Module module) {
        if (this.persistStates && this.storage != null && this.storage.isConnected()) {
            this.storage.saveModuleState(module.getIdentifier(), module.getState());
        }
    }

    private @NotNull Module getModuleOrThrow(final @NotNull String identifier) throws ModuleException {
        return this.getModule(identifier).orElseThrow(() -> new ModuleException("Module with identifier '" + identifier + "' is not registered"));
    }
}