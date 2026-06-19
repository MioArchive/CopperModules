package net.javamio.coppermodule.velocity.module;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;
import lombok.Getter;
import net.javamio.coppermodule.common.module.AbstractModule;
import net.javamio.coppermodule.common.module.exception.ModuleException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class VelocityModule extends AbstractModule {

    @Getter
    private final @NotNull Object plugin;
    private final @NotNull ProxyServer server;
    private final @NotNull List<Object> registeredListeners;
    private final @NotNull List<ScheduledTask> registeredTasks;

    protected VelocityModule(final @NotNull String identifier, final @NotNull String displayName, final @NotNull Object plugin, final @NotNull ProxyServer server) {
        super(identifier, displayName);
        this.plugin = plugin;
        this.server = server;
        this.registeredListeners = new ArrayList<>();
        this.registeredTasks = new ArrayList<>();
    }

    protected void registerListener(final @NotNull Object listener) {
        this.server.getEventManager().register(this.plugin, listener);
        this.registeredListeners.add(listener);
    }

    protected void registerTask(final @NotNull ScheduledTask task) {
        this.registeredTasks.add(task);
    }

    @Override
    public void onDisable() throws ModuleException {
        for (final Object listener : this.registeredListeners) {
            this.server.getEventManager().unregisterListener(this.plugin, listener);
        }
        this.registeredListeners.clear();

        for (final ScheduledTask task : this.registeredTasks) {
            if (task.status() == TaskStatus.SCHEDULED) {
                task.cancel();
            }
        }
        this.registeredTasks.clear();

        super.onDisable();
    }
}
