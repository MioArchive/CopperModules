package net.javamio.coppermodule.bukkit.module;

import lombok.Getter;
import net.javamio.coppermodule.common.module.AbstractModule;
import net.javamio.coppermodule.common.module.exception.ModuleException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class BukkitModule extends AbstractModule {

    @Getter
    private final @NotNull JavaPlugin plugin;
    private final @NotNull List<Listener> registeredListeners;
    private final @NotNull List<BukkitTask> registeredTasks;

    protected BukkitModule(final @NotNull String identifier, final @NotNull String displayName, final @NotNull JavaPlugin plugin) {
        super(identifier, displayName);
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