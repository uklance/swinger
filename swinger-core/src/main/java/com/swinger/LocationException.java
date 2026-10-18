package com.swinger;

import com.swinger.model.Location;

public class LocationException extends Exception {
    private final Location location;

    public LocationException(Location location, Throwable cause) {
        super(formatMessage(location, cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage()), cause);
        this.location = location;
    }

    public LocationException(Location location, String message) {
        super(formatMessage(location, message));
        this.location = location;
    }

    public LocationException(Location location, String message, Throwable cause) {
        super(formatMessage(location, message), cause);
        this.location = location;
    }

    private static String formatMessage(Location location, String message) {
        return String.format("%s [publicId=%s, line=%s, column=%s]", message, location.getPublicId(), location.getLineNumber(), location.getColumnNumber());
    }

    public Location getLocation() {
        return location;
    }
}
