package com.swinger.sax;

import com.swinger.model.Location;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.xml.sax.Attributes;

@RequiredArgsConstructor
public abstract class AbstractTemplateNode implements TemplateNode {
    @Getter private final String name;
    @Getter private final Attributes attributes;
    @Getter private final Location location;
    private StringBuilder characters;

    public void onCharacters(char[] ch, int start, int length) {
        if (characters == null) {
            characters = new StringBuilder(length);
        }
        characters.append(ch, start, length);
    }

    public abstract void onChild(TemplateNode child);

    @Override
    public String getText() {
        return characters == null ? null : characters.toString();
    }
}
