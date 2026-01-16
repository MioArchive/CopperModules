package net.javamio.coppermodule.common.module.exception;

public class ModuleException extends Exception {

    public ModuleException(final String message) {
        super(message);
    }

    public ModuleException(final String message, final Throwable cause) {
        super(message, cause);
    }
}