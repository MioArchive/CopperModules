package net.javamio.coppermodule.common.module;

import lombok.Getter;
import net.javamio.coppermodule.common.module.exception.ModuleException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class AbstractModule implements Module {

    @Getter
    private final String identifier;
    private final String displayName;
    private final Set<String> dependencies;
    private final Set<SubModule> subModules;
    private ModuleState state;

    protected AbstractModule(final String identifier, final String displayName) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.dependencies = new HashSet<>();
        this.subModules = new HashSet<>();
        this.state = ModuleState.UNLOADED;
    }

    @Override
    public Set<String> getDependencies() {
        return Collections.unmodifiableSet(this.dependencies);
    }

    @Override
    public Set<SubModule> getSubModules() {
        return Collections.unmodifiableSet(this.subModules);
    }

    @Override
    public void setState(final ModuleState state) {
        this.state = state;
    }

    protected void addDependency(final String moduleIdentifier) {
        this.dependencies.add(moduleIdentifier);
    }

    protected void registerSubModule(final SubModule subModule) {
        this.subModules.add(subModule);
    }

    @Override
    public void onLoad() throws ModuleException {
    }

    @Override
    public void onEnable() throws ModuleException {
    }

    @Override
    public void onDisable() throws ModuleException {
    }

    @Override
    public void onUnload() throws ModuleException {
    }
}