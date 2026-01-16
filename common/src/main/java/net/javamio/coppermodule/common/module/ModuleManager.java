package net.javamio.coppermodule.common.module;

import net.javamio.coppermodule.common.module.exception.ModuleException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleManager {

    private final Map<String, Module> modules;

    public ModuleManager() {
        this.modules = new ConcurrentHashMap<>();
    }

    public void registerModule(final Module module) throws ModuleException {
        if (this.modules.containsKey(module.getIdentifier())) {
            throw new ModuleException("Module with identifier '" + module.getIdentifier() + "' is already registered");
        }

        this.modules.put(module.getIdentifier(), module);
    }

    public void unregisterModule(final String identifier) throws ModuleException {
        final Module module = this.modules.get(identifier);
        if (module == null) {
            throw new ModuleException("Module with identifier '" + identifier + "' is not registered");
        }

        if (module.getState() != ModuleState.UNLOADED) {
            throw new ModuleException("Cannot unregister module '" + identifier + "' while it is not unloaded");
        }

        this.modules.remove(identifier);
    }

    public Optional<Module> getModule(final String identifier) {
        return Optional.ofNullable(this.modules.get(identifier));
    }

    public Collection<Module> getModules() {
        return Collections.unmodifiableCollection(this.modules.values());
    }

    public void loadModule(final String identifier) throws ModuleException {
        final Module module = this.getModuleOrThrow(identifier);

        if (module.getState() != ModuleState.UNLOADED) {
            throw new ModuleException("Module '" + identifier + "' is already loaded");
        }

        for (final String dependency : module.getDependencies()) {
            final Module dependencyModule = this.getModuleOrThrow(dependency);
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

    public void enableModule(final String identifier) throws ModuleException {
        final Module module = this.getModuleOrThrow(identifier);

        if (module.getState() == ModuleState.ENABLED) {
            throw new ModuleException("Module '" + identifier + "' is already enabled");
        }

        if (module.getState() == ModuleState.UNLOADED) {
            this.loadModule(identifier);
        }

        for (final String dependency : module.getDependencies()) {
            final Module dependencyModule = this.getModuleOrThrow(dependency);
            if (dependencyModule.getState() != ModuleState.ENABLED) {
                throw new ModuleException("Cannot enable module '" + identifier + "' because dependency '" + dependency + "' is not enabled");
            }
        }

        try {
            module.onEnable();
            module.setState(ModuleState.ENABLED);

            for (final SubModule subModule : module.getSubModules()) {
                this.enableModule(subModule.getIdentifier());
            }
        } catch (final Exception exception) {
            module.setState(ModuleState.ERROR);
            throw new ModuleException("Failed to enable module '" + identifier + "'", exception);
        }
    }

    public void disableModule(final String identifier) throws ModuleException {
        final Module module = this.getModuleOrThrow(identifier);

        if (module.getState() != ModuleState.ENABLED) {
            throw new ModuleException("Module '" + identifier + "' is not enabled");
        }

        final List<Module> dependentModules = this.modules.values().stream()
                .filter(m -> m.getDependencies().contains(identifier))
                .filter(m -> m.getState() == ModuleState.ENABLED)
                .toList();

        for (final Module dependent : dependentModules) {
            this.disableModule(dependent.getIdentifier());
        }

        for (final SubModule subModule : module.getSubModules()) {
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

    public void unloadModule(final String identifier) throws ModuleException {
        final Module module = this.getModuleOrThrow(identifier);

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

    public void reloadModule(final String identifier) throws ModuleException {
        final Module module = this.getModuleOrThrow(identifier);
        final ModuleState previousState = module.getState();

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

    private Module getModuleOrThrow(final String identifier) throws ModuleException {
        return this.getModule(identifier)
                .orElseThrow(() -> new ModuleException("Module with identifier '" + identifier + "' is not registered"));
    }
}