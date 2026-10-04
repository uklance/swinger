def outputDirectory = new File(generatedSourcesDirectory.toString())
def componentClasses = swingComponentClasses.toString()
        .split(',')
        .collect { it.trim() }
        .findAll { !it.isEmpty() }

if (componentClasses.isEmpty()) {
    throw new IllegalArgumentException('At least one Swing component class must be configured')
}

if (outputDirectory.exists() && !outputDirectory.deleteDir()) {
    throw new IOException("Could not clear generated source directory: ${outputDirectory}")
}

def generatedNames = new HashSet<String>()
componentClasses.each { componentClass ->
    int separator = componentClass.lastIndexOf('.')
    if (separator < 1 || separator == componentClass.length() - 1) {
        throw new IllegalArgumentException("Expected a fully qualified component class name: ${componentClass}")
    }

    def javaClassName = componentClass.substring(separator + 1)
    def wrapperName = javaClassName.startsWith('J') &&
            javaClassName.length() > 1 &&
            Character.isUpperCase(javaClassName.charAt(1))
            ? javaClassName.substring(1)
            : javaClassName
    if (!generatedNames.add(wrapperName)) {
        throw new IllegalArgumentException("Multiple Swing classes map to wrapper ${wrapperName}")
    }

    def fieldName = wrapperName.substring(0, 1).toLowerCase(Locale.ROOT) + wrapperName.substring(1)
    def sourceDirectory = new File(outputDirectory, 'com/swinger/component')
    if (!sourceDirectory.mkdirs() && !sourceDirectory.isDirectory()) {
        throw new IOException("Could not create generated source directory: ${sourceDirectory}")
    }

    def source = """\
package com.swinger.component;

import com.swinger.annotation.AfterRenderBody;
import com.swinger.annotation.BeforeRenderBody;
import com.swinger.annotation.Property;
import com.swinger.annotation.ProxyProperties;
import com.swinger.api.SwingWriter;
import lombok.Getter;
import ${componentClass};

public class ${wrapperName} {
    @Getter
    @ProxyProperties(exclude = "setDisplayedMnemonic")
    private final ${javaClassName} ${fieldName} = new ${javaClassName}();

    @Property
    private Object constraints;

    @BeforeRenderBody
    public void beforeRenderBody(SwingWriter writer) {
        writer.startElement(${fieldName}, constraints);
    }

    @AfterRenderBody
    public void afterRenderBody(SwingWriter writer) {
        writer.endElement();
    }
}
"""
    new File(sourceDirectory, "${wrapperName}.java").setText(source, 'UTF-8')
}
