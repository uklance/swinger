package com.swinger.sax;

import com.swinger.io.Resource;
import lombok.AllArgsConstructor;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class SaxComponentTemplateParser implements ComponentTemplateParser  {
    private final Map<String, ComponentTemplate> cache = new ConcurrentHashMap<>();
    private final SAXParserFactory saxParserFactory;

    @Override
    public ComponentTemplate parse(Resource resource) throws Exception {
        String resourceId = resource.getId();
        ComponentTemplate cached = cache.get(resourceId);
        if (cached != null) {
            return cached;
        }
        return cache.putIfAbsent(resourceId, create(resource));
    }

    private ComponentTemplate create(Resource resource) throws ParserConfigurationException, SAXException, IOException {
        ComponentSaxHandler saxHandler = new ComponentSaxHandler();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        try (InputStream in = resource.getInputStream()) {
            InputSource inputSource = new InputSource(in);
            inputSource.setPublicId(resource.getPath());
            inputSource.setSystemId(resource.getPath());
            saxParser.parse(inputSource, saxHandler);
        }
        ComponentTemplateNode rootNode = (ComponentTemplateNode) saxHandler.getRootNode();
        return () -> rootNode;
    }
}
