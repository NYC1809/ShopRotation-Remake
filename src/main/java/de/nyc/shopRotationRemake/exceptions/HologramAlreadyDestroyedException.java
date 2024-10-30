package de.nyc.shopRotationRemake.exceptions;

public class HologramAlreadyDestroyedException extends RuntimeException {

    public HologramAlreadyDestroyedException() {
        super("The hologram has already been destroyed.");
    }

    public HologramAlreadyDestroyedException(String message) {
        super(message);
    }
}

