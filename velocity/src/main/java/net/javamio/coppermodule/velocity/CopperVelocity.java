package net.javamio.coppermodule.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.javamio.coppermodule.common.module.ModuleManager;
import net.javamio.coppermodule.common.module.exception.ModuleException;
import net.javamio.coppermodule.common.module.storage.ModuleStorage;
import net.javamio.coppermodule.common.module.storage.impl.MySqlStorage;
import net.javamio.coppermodule.velocity.module.impl.ChatModule;

@Getter
public class CopperVelocity {

    @Getter
    private static CopperVelocity instance;

    private final ProxyServer server;
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

    @Inject
    public CopperVelocity(ProxyServer server) {
        instance = this;
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent ignoredEvent) {

        // There are two ready-made ModuleStorage implementations, one using MemoryStorage and one using MySQL as storage.
        // If desired, you can also write your own implementation. To do so, simply check out our read-me!

        // final ModuleStorage moduleStorage = new MemoryStorage();
        final ModuleStorage moduleStorage = new MySqlStorage(host, port, database, username, password, tableName);
        moduleManager = new ModuleManager(moduleStorage, persistStates);

        try {
            moduleManager.registerModule(new ChatModule(instance, server));
        } catch (ModuleException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent ignoredEvent) {
    }
}