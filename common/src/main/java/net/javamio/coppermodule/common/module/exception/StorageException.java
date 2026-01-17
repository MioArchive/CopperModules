package net.javamio.coppermodule.common.module.exception;

import org.jetbrains.annotations.NotNull;

public class StorageException extends Exception {

    public StorageException(@NotNull final String message) {
        super(message);
    }

    public StorageException(@NotNull final String message, @NotNull final Throwable cause) {
        super(message, cause);
    }
}