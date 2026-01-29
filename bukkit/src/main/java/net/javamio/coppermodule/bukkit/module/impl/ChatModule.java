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
