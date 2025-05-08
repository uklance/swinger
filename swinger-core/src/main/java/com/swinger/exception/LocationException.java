package com.swinger.exception;

import com.swinger.model.Location;
import lombok.Getter;

public class LocationException extends RuntimeException {
    @Getter
    private final Location location;

    public LocationException(Location location, String msg, Throwable cause) {
        super(String.format("%s (line:%s, col:%s)", msg, location.getLineNumber(), location.getColumnNumber()), cause);
        this.location = location;
    }
}
