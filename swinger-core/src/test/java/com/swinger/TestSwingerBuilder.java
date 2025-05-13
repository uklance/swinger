package com.swinger;

import com.swinger.api.*;
import com.swinger.impl.*;
import com.swinger.test.ButtonABC;

import javax.swing.*;
import javax.xml.parsers.SAXParserFactory;
import java.util.List;
import java.util.Map;

public class TestSwingerBuilder {
    public Swinger build() {
        EventManager eventManager = new DefaultEventManager();
        MemberAccessor memberAccessor = new ReflectionMemberAccessor();
        Map<String, ComponentSource> componentSources = Map.of(
                "panel", new SupplierComponentSource(JPanel::new),
                "label", new SupplierComponentSource(JLabel::new),
                "button", new SupplierComponentSource(JButton::new),
                "text-field", new SupplierComponentSource(JTextField::new),
                "text-area", new SupplierComponentSource(JTextArea::new),
                "split-pane", new SupplierComponentSource(JSplitPane::new),
                "button-abc", new SwingerComponentSource(ButtonABC.class)
        );
        List<ControllerFieldHandler> fieldHandlers = List.of();
        List<ControllerMethodHandler> methodHandlers = List.of(
                new OnEventControllerMethodHandler(eventManager)
        );
        ComponentFactory componentFactory = new DefaultComponentFactory(componentSources);
        BindingRegistry bindingRegistry = new DefaultBindingRegistry(Map.of(
                "prop", new PropertyBinding(memberAccessor),
                "event", new EventBinding(eventManager),
                "new", new NewBinding(),
                "literal", new LiteralBinding()
        ));
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);

        return new Swinger(componentFactory, bindingRegistry, memberAccessor, saxParserFactory, fieldHandlers, methodHandlers);
    }
}
