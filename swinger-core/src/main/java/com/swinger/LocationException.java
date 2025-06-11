package com.swinger;

import com.swinger.model.Location;

public class LocationException extends Exception {
    private final Location location;

    public LocationException(Location location, Throwable cause) {
        super(String.format("%s [publicId=%s, line=%s, column=%s]", cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage(), location.getPublicId(), location.getLineNumber(), location.getColumnNumber()), cause);
        this.location = location;
    }

    public LocationException(Location location, String message) {
        super(String.format("%s [publicId=%s, line=%s, column=%s]", message, location.getPublicId(), location.getLineNumber(), location.getColumnNumber()));
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
