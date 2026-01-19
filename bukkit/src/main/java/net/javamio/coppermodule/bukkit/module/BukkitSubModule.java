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

package net.javamio.coppermodule.bukkit.module;

import lombok.Getter;
import net.javamio.coppermodule.common.module.AbstractSubModule;
import net.javamio.coppermodule.common.module.Module;
import net.javamio.coppermodule.common.module.exception.ModuleException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class BukkitSubModule extends AbstractSubModule {

    @Getter
    private final @NotNull JavaPlugin plugin;
    private final @NotNull List<Listener> registeredListeners;
    private final @NotNull List<BukkitTask> registeredTasks;

    protected BukkitSubModule(final @NotNull String identifier, final @NotNull String displayName, final @NotNull Module parentModule, final @NotNull JavaPlugin plugin) {
        super(identifier, displayName, parentModule);
        this.plugin = plugin;
        this.registeredListeners = new ArrayList<>();
        this.registeredTasks = new ArrayList<>();
    }

    protected void registerListener(final @NotNull Listener listener) {
        this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
        this.registeredListeners.add(listener);
    }

    protected void registerTask(final @NotNull BukkitTask task) {
        this.registeredTasks.add(task);
    }

    @Override
    public void onDisable() throws ModuleException {
        for (final Listener listener : this.registeredListeners) {
            HandlerList.unregisterAll(listener);
        }
        this.registeredListeners.clear();

        for (final BukkitTask task : this.registeredTasks) {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
        this.registeredTasks.clear();

        super.onDisable();
    }
}