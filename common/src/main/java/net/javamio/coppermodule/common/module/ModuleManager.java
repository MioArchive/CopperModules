package net.javamio.coppermodule.common.module;

import net.javamio.coppermodule.common.module.exception.ModuleException;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleManager {

    private final @NotNull Map<String, Module> modules;

    public ModuleManager() {
        this.modules = new ConcurrentHashMap<>();
    }

    public void registerModule(@NotNull final Module module) throws ModuleException {
        final @NotNull String identifier = module.getIdentifier();

        if (this.modules.containsKey(identifier)) {
            throw new ModuleException("Module with identifier '" + identifier + "' is already registered");
        }

        this.modules.put(identifier, module);
    }

    public void unregisterModule(@NotNull final String identifier) throws ModuleException {
        final @NotNull Module module = this.modules.get(identifier);
        if (module == null) {
            throw new ModuleException("Module with identifier '" + identifier + "' is not registered");
        }

        if (module.getState() != ModuleState.UNLOADED) {
            throw new ModuleException("Cannot unregister module '" + identifier + "' while it is not unloaded");
        }

        this.modules.remove(identifier);
    }

    public @NotNull Optional<@NotNull Module> getModule(@NotNull final String identifier) {
        return Optional.ofNullable(this.modules.get(identifier));
    }

    public @NotNull Collection<@NotNull Module> getModules() {
        return Collections.unmodifiableCollection(this.modules.values());
    }

    public void loadModule(@NotNull final String identifier) throws ModuleException {
        final @NotNull Module module = this.getModuleOrThrow(identifier);

        if (module.getState() != ModuleState.UNLOADED) {
            throw new ModuleException("Module '" + identifier + "' is already loaded");
        }

        for (final @NotNull String dependency : module.getDependencies()) {
            final @NotNull Module dependencyModule = this.getModuleOrThrow(dependency);
            if (dependencyModule.getState() == ModuleState.UNLOADED) {
                this.loadModule(dependency);
            }
        }

        try {
            module.onLoad();
            module.setState(ModuleState.LOADED);
        } catch (final Exception exception) {
            module.setState(ModuleState.ERROR);
            throw new ModuleException("Failed to load module '" + identifier + "'", exception);
        }
    }

    public void enableModule(@NotNull final String identifier) throws ModuleException {
        final @NotNull Module module = this.getModuleOrThrow(identifier);

        if (module.getState() == ModuleState.ENABLED) {
            throw new ModuleException("Module '" + identifier + "' is already enabled");
        }

        if (module.getState() == ModuleState.UNLOADED) {
            this.loadModule(identifier);
        }

        for (final @NotNull String dependency : module.getDependencies()) {
            final @NotNull Module dependencyModule = this.getModuleOrThrow(dependency);
            if (dependencyModule.getState() != ModuleState.ENABLED) {
                throw new ModuleException("Cannot enable module '" + identifier + "' because dependency '" + dependency + "' is not enabled");
            }
        }

        try {
            module.onEnable();
            module.setState(ModuleState.ENABLED);

            for (final @NotNull SubModule subModule : module.getSubModules()) {
                this.enableModule(subModule.getIdentifier());
            }
        } catch (final Exception exception) {
            module.setState(ModuleState.ERROR);
            throw new ModuleException("Failed to enable module '" + identifier + "'", exception);
        }
    }

    public void disableModule(@NotNull final String identifier) throws ModuleException {
        final @NotNull Module module = this.getModuleOrThrow(identifier);

        if (module.getState() != ModuleState.ENABLED) {
            throw new ModuleException("Module '" + identifier + "' is not enabled");
        }

        final @NotNull List<@NotNull Module> dependentModules = this.modules.values().stream()
                .filter(m -> m.getDependencies().contains(identifier))
                .filter(m -> m.getState() == ModuleState.ENABLED)
                .toList();

        for (final @NotNull Module dependent : dependentModules) {
            this.disableModule(dependent.getIdentifier());
        }

        for (final @NotNull SubModule subModule : module.getSubModules()) {
            if (subModule.getState() == ModuleState.ENABLED) {
                this.disableModule(subModule.getIdentifier());
            }
        }

        try {
            module.onDisable();
            module.setState(ModuleState.DISABLED);
        } catch (final Exception exception) {
            module.setState(ModuleState.ERROR);
            throw new ModuleException("Failed to disable module '" + identifier + "'", exception);
        }
    }

    public void unloadModule(@NotNull final String identifier) throws ModuleException {
        final @NotNull Module module = this.getModuleOrThrow(identifier);

        if (module.getState() == ModuleState.ENABLED) {
            this.disableModule(identifier);
        }

        if (module.getState() == ModuleState.UNLOADED) {
            throw new ModuleException("Module '" + identifier + "' is already unloaded");
        }

        try {
            module.onUnload();
            module.setState(ModuleState.UNLOADED);
        } catch (final Exception exception) {
            module.setState(ModuleState.ERROR);
            throw new ModuleException("Failed to unload module '" + identifier + "'", exception);
        }
    }

    public void reloadModule(@NotNull final String identifier) throws ModuleException {
        final @NotNull Module module = this.getModuleOrThrow(identifier);
        final @NotNull ModuleState previousState = module.getState();

        if (previousState == ModuleState.ENABLED) {
            this.disableModule(identifier);
        }

        if (module.getState() != ModuleState.UNLOADED) {
            this.unloadModule(identifier);
        }

        this.loadModule(identifier);

        if (previousState == ModuleState.ENABLED) {
            this.enableModule(identifier);
        }
    }

    private @NotNull Module getModuleOrThrow(@NotNull final String identifier) throws ModuleException {
        return this.getModule(identifier).orElseThrow(() -> new ModuleException("Module with identifier '" + identifier + "' is not registered"));
    }
}
