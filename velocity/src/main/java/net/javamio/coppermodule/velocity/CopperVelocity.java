package net.javamio.coppermodule.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.ProxyServer;

public class CopperVelocity {
    private final ProxyServer server;

    @Inject
    public CopperVelocity(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent ignoredEvent) {
    }

    @Subscribe()
    public void onProxyShutdown(ProxyShutdownEvent ignoredEvent) {
    }
}