package net.javamio.coppermodule.common.module;

import net.javamio.coppermodule.common.module.exception.ModuleException;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Module {

    @NotNull String getIdentifier();

    @NotNull String getDisplayName(); // TODO

    @NotNull ModuleState getState();

    @NotNull Set<@NotNull String> getDependencies();

    @NotNull Set<@NotNull SubModule> getSubModules();

    void onLoad() throws ModuleException;

    void onEnable() throws ModuleException;

    void onDisable() throws ModuleException;

    void onUnload() throws ModuleException;

    void setState(@NotNull ModuleState state);
}
