package net.javamio.coppermodule.common.module.exception;

import org.jetbrains.annotations.NotNull;

public class ModuleException extends Exception {

    public ModuleException(@NotNull final String message) {
        super(message);
    }

    public ModuleException(@NotNull final String message, @NotNull final Throwable cause) {
        super(message, cause);
    }
}