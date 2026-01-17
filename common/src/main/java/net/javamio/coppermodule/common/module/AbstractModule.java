package net.javamio.coppermodule.common.module;

import lombok.Getter;
import net.javamio.coppermodule.common.module.exception.ModuleException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class AbstractModule implements Module {

    private final @NotNull String identifier;
    private final @NotNull String displayName;
    private final @NotNull Set<String> dependencies;
    private final @NotNull Set<SubModule> subModules;
    private @NotNull ModuleState state;

    protected AbstractModule(@NotNull final String identifier, @NotNull final String displayName) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.dependencies = new HashSet<>();
        this.subModules = new HashSet<>();
        this.state = ModuleState.UNLOADED;
    }

    @Override
    @NotNull
    public Set<String> getDependencies() {
        return Collections.unmodifiableSet(this.dependencies);
    }

    @Override
    @NotNull
    public Set<SubModule> getSubModules() {
        return Collections.unmodifiableSet(this.subModules);
    }

    @Override
    public void setState(@NotNull final ModuleState state) {
        this.state = state;
    }

    protected void addDependency(@NotNull final String moduleIdentifier) {
        this.dependencies.add(moduleIdentifier);
    }

    protected void registerSubModule(@NotNull final SubModule subModule) {
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
