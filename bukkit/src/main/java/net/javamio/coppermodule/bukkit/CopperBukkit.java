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

package net.javamio.coppermodule.bukkit;

import lombok.Getter;
import net.javamio.coppermodule.bukkit.module.impl.ChatModule;
import net.javamio.coppermodule.common.module.ModuleManager;
import net.javamio.coppermodule.common.module.exception.ModuleException;
import net.javamio.coppermodule.common.module.storage.ModuleStorage;
import net.javamio.coppermodule.common.module.storage.impl.MySqlStorage;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CopperBukkit extends JavaPlugin {

    @Getter
    private static CopperBukkit instance;
    private ModuleManager moduleManager;

    // Configuration section for the storage module; theoretically, this can also be read from the config.
    // This entire section is not necessary when using MemoryStorage.
    private final boolean persistStates = true;
    private final String host = "localhost";
    private final int port = 3306;
    private final String database = "coppermodule";
    private final String username = "coppermodule";
    private final String password = "coppermodule";
    private final String tableName = "module_states";

    public CopperBukkit() {
        instance = this;
    }

    @Override
    public void onEnable() {

        // There are two ready-made ModuleStorage implementations, one using MemoryStorage and one using MySQL as storage.
        // If desired, you can also write your own implementation. To do so, simply check out our read-me!

        // final ModuleStorage moduleStorage = new MemoryStorage();
        final ModuleStorage moduleStorage = new MySqlStorage(host, port, database, username, password, tableName);
        moduleManager = new ModuleManager(moduleStorage, persistStates);

        try {
            moduleManager.registerModule(new ChatModule(instance));
        } catch (ModuleException e) {
            throw new RuntimeException(e);
        }
    }

}