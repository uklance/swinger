package com.swinger.sax;

import com.swinger.io.Resource;
import lombok.AllArgsConstructor;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

@AllArgsConstructor
public class SaxComponentTemplateParser implements ComponentTemplateParser  {
    private final SAXParserFactory saxParserFactory;

    @Override
    public ComponentTemplate parse(Resource resource) throws Exception {
        ComponentSaxHandler saxHandler = new ComponentSaxHandler();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        try (InputStream in = resource.getInputStream()) {
            InputSource inputSource = new InputSource(in);
            inputSource.setSystemId(resource.getPath());
            saxParser.parse(inputSource, saxHandler);
        }
        TemplateNode rootNode = saxHandler.getRootNode();
        return () -> rootNode;
    }
}
