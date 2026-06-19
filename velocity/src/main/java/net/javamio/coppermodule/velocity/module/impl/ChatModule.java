package net.javamio.coppermodule.velocity.module.impl;

import com.velocitypowered.api.proxy.ProxyServer;
import net.javamio.coppermodule.common.module.exception.ModuleException;
import net.javamio.coppermodule.velocity.module.VelocityModule;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class ChatModule extends VelocityModule {

    public ChatModule(@NotNull Object plugin, @NotNull ProxyServer server) {
        super("chat-module", "Chat Module", plugin, server);
    }

    @Override
    public void onLoad() throws ModuleException {
        registerSubModule(new ChatFilterSubModule(this, getPlugin(), getServer()));
    }

    @Override
    public void onEnable() throws ModuleException {
        Logger.getAnonymousLogger().info("Successfully enabled Chat Module");
    }

}
