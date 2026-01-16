package net.javamio.coppermodule.bukkit;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class CopperBukkit extends JavaPlugin {
    private static CopperBukkit instance;

    public CopperBukkit() {
        instance = this;
    }

    @Override
    public void onEnable() {
    }

}