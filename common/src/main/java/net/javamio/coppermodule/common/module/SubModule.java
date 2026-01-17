package net.javamio.coppermodule.common.module;

import org.jetbrains.annotations.NotNull;

public interface SubModule extends Module {

    @NotNull Module getParentModule();
}