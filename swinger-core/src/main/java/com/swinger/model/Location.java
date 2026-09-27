package com.swinger.model;

import lombok.Getter;
import org.xml.sax.Locator;

@Getter
public class Location {
    private final String publicId;
    private final String systemId;
    private final int lineNumber;
    private final int columnNumber;

    public Location(Locator locator) {
        this.publicId = locator.getPublicId();
        this.systemId = locator.getSystemId();
        this.lineNumber = locator.getLineNumber();
        this.columnNumber = locator.getColumnNumber();
    }

    @Override
    public String toString() {
        int slashIndex = publicId.lastIndexOf('/');
        String file = slashIndex >= 0 ? publicId.substring(slashIndex + 1) : publicId;
        return String.format("%s[%s:%s]", file, lineNumber, columnNumber);
    }
}
