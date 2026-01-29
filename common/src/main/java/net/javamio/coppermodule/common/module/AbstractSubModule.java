package net.javamio.coppermodule.common.module;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class AbstractSubModule extends AbstractModule implements SubModule {

    @Getter
    @NotNull
    private final Module parentModule;

    protected AbstractSubModule(@NotNull final String identifier, @NotNull final String displayName, @NotNull final Module parentModule) {
        super(identifier, displayName);
        this.parentModule = parentModule;
    }
}