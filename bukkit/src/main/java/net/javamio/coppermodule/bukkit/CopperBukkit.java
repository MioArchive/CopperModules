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