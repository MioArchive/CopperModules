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

package net.javamio.coppermodule.bukkit.module.impl;

import net.javamio.coppermodule.bukkit.module.BukkitModule;
import net.javamio.coppermodule.common.module.exception.ModuleException;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class ChatModule extends BukkitModule {

    public ChatModule(@NotNull JavaPlugin plugin) {
        super("chat-module", "Chat Module", plugin);
    }

    @Override
    public void onLoad() throws ModuleException {
        registerSubModule(new ChatFilterSubModule(this, getPlugin()));
    }

    @Override
    public void onEnable() throws ModuleException {
        Logger.getAnonymousLogger().info("Successfully enabled Chat Module");
    }

}
