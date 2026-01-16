package net.javamio.coppermodule.common.module;

import lombok.Getter;

@Getter
public abstract class AbstractSubModule extends AbstractModule implements SubModule {

    @Getter
    private final Module parentModule;

    protected AbstractSubModule(final String identifier, final String displayName, final Module parentModule) {
        super(identifier, displayName);
        this.parentModule = parentModule;
    }
}