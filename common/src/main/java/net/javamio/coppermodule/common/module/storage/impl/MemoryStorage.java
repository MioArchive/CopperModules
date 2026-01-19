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

package net.javamio.coppermodule.common.module.storage.impl;

import net.javamio.coppermodule.common.module.ModuleState;
import net.javamio.coppermodule.common.module.storage.ModuleStorage;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryStorage implements ModuleStorage {

    private final Map<String, ModuleState> moduleStates;

    public MemoryStorage() {
        this.moduleStates = new ConcurrentHashMap<>();
    }

    @Override
    public void saveModuleState(final @NotNull String moduleIdentifier, final @NotNull ModuleState state) {
        this.moduleStates.put(moduleIdentifier, state);
    }

    @Override
    public @NotNull Optional<ModuleState> loadModuleState(final @NotNull String moduleIdentifier) {
        return Optional.ofNullable(this.moduleStates.get(moduleIdentifier));
    }

    @Override
    public @NotNull Map<String, ModuleState> loadAllModuleStates() {
        return new HashMap<>(this.moduleStates);
    }

    @Override
    public void deleteModuleState(final @NotNull String moduleIdentifier) {
        this.moduleStates.remove(moduleIdentifier);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void close() {
        this.moduleStates.clear();
    }

    @Override
    public boolean isConnected() {
        return true;
    }
}