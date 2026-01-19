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

import net.javamio.coppermodule.common.module.exception.ModuleException;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Module {

    @NotNull String getIdentifier();

    @NotNull String getDisplayName(); // TODO

    @NotNull ModuleState getState();

    @NotNull Set<@NotNull String> getDependencies();

    @NotNull Set<@NotNull SubModule> getSubModules();

    void onLoad() throws ModuleException;

    void onEnable() throws ModuleException;

    void onDisable() throws ModuleException;

    void onUnload() throws ModuleException;

    void setState(@NotNull ModuleState state);
}
