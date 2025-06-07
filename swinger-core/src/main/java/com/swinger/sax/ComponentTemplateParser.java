package com.swinger.sax;

import com.swinger.io.Resource;

public interface ComponentTemplateParser {
    ComponentTemplate parse(Resource resource) throws Exception;
}
