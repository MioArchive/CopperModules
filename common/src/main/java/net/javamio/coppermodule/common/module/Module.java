package net.javamio.coppermodule.common.module;

import net.javamio.coppermodule.common.module.exception.ModuleException;

import java.util.Set;

public interface Module {

    String getIdentifier();

    String getDisplayName(); // - TODO

    ModuleState getState();

    Set<String> getDependencies();

    Set<SubModule> getSubModules();

    void onLoad() throws ModuleException;

    void onEnable() throws ModuleException;

    void onDisable() throws ModuleException;

    void onUnload() throws ModuleException;

    void setState(ModuleState state);
}