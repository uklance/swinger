package com.swinger.impl;

import com.swinger.LocationException;
import com.swinger.annotation.*;
import com.swinger.api.*;
import com.swinger.sax.ComponentTemplateNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class DefaultComponentRenderer implements ComponentRenderer {
    @RequiredArgsConstructor
    @Getter
    static class RenderNode {
        private final String name;
        private final RenderPredicate predicate;
        private final RenderAction action;
        private RenderNode positiveNode;
        private RenderNode negativeNode;

        public RenderNode(String name, RenderPredicate predicate) {
            this(name, predicate, null);
        }

        void setChildren(RenderNode positiveNode, RenderNode negativeNode) {
            this.positiveNode = positiveNode;
            this.negativeNode = negativeNode;
        }
    }

    interface RenderPredicate {
        boolean test(Object instance, Controller controller, SwingWriter writer) throws Exception;
    }

    interface RenderAction {
        void render(ComponentResources resources, List<ComponentTemplateNode> body, SwingWriter writer) throws Exception;
    }

    private final RenderNode setupRender;
    private final RenderNode beginRender;
    private final RenderNode beforeRenderTemplate;
    private final RenderNode beforeRenderBody;
    private final RenderNode afterRenderBody;
    private final RenderNode afterRenderTemplate;
    private final RenderNode afterRender;
    private final RenderNode cleanupRender;

    private final ComponentFactory componentFactory;

    public DefaultComponentRenderer(ComponentFactory componentFactory) {
        setupRender = new RenderNode("setupRender", (instance, controller, writer) -> invokeLifecycle(instance, SetupRender.class, writer, controller,
                Controller::setupRender));
        beginRender = new RenderNode("beginRender", (instance, controller, writer) -> invokeLifecycle(instance, BeginRender.class, writer, controller,
                Controller::beginRender));
        beforeRenderTemplate = new RenderNode("beforeRenderTemplate", (instance, controller, writer) -> invokeLifecycle(instance, BeforeRenderTemplate.class, writer, controller,
                Controller::beforeRenderTemplate), this::renderTemplate);
        beforeRenderBody = new RenderNode("beforeRenderBody", (instance, controller, writer) -> invokeLifecycle(instance, BeforeRenderBody.class, writer, controller,
                Controller::beforeRenderBody), this::renderBody);
        afterRenderBody = new RenderNode("afterRenderBody", (instance, controller, writer) -> invokeLifecycle(instance, AfterRenderBody.class, writer, controller,
                Controller::afterRenderBody));
        afterRenderTemplate = new RenderNode("afterRenderTemplate", (instance, controller, writer) -> invokeLifecycle(instance, AfterRenderTemplate.class, writer, controller,
                Controller::afterRenderTemplate));
        afterRender = new RenderNode("afterRender", (instance, controller, writer) -> invokeLifecycle(instance, AfterRender.class, writer, controller,
                Controller::afterRender));
        cleanupRender = new RenderNode("cleanupRender", (instance, controller, writer) -> invokeLifecycle(instance, CleanupRender.class, writer, controller,
                Controller::cleanupRender));

        setupRender.setChildren(beginRender, cleanupRender);
        beginRender.setChildren(beforeRenderTemplate, afterRender);
        beforeRenderTemplate.setChildren(beforeRenderBody, afterRenderTemplate);
        beforeRenderBody.setChildren(afterRenderBody, afterRenderBody);
        afterRenderBody.setChildren(afterRenderTemplate, beforeRenderBody);
        afterRenderTemplate.setChildren(afterRender, beforeRenderTemplate);
        afterRender.setChildren(cleanupRender, beginRender);
        cleanupRender.setChildren(null, setupRender);

        this.componentFactory = componentFactory;
    }

    private boolean invokeLifecycle(Object instance, Class<? extends Annotation> annotation, SwingWriter writer,
                                    Controller controller,
                                    ControllerLifecycleFallback fallback) throws Exception {
        return controller == null || fallback.invoke(controller, instance, writer);
    }

    @FunctionalInterface
    private interface ControllerLifecycleFallback {
        boolean invoke(Controller controller, Object instance, SwingWriter writer) throws Exception;
    }

    @Override
    public void render(ComponentResources resources, SwingWriter writer) throws Exception {
        render(resources, Collections.emptyList(), writer);
    }

    protected void render(ComponentResources resources, List<ComponentTemplateNode> body, SwingWriter writer) throws Exception {
        Object instance = resources.getComponentInstance();
        Controller controller = resources.getController();
        RenderNode currentNode = setupRender;
        while (currentNode != null) {
            boolean proceed = currentNode.getPredicate().test(instance, controller, writer);
            if (proceed) {
                if (currentNode.getAction() != null) {
                    currentNode.getAction().render(resources, body, writer);
                }
                currentNode = currentNode.getPositiveNode();
            } else {
                currentNode = currentNode.getNegativeNode();
            }
        }
    }

    protected void renderTemplate(ComponentResources resources, List<ComponentTemplateNode> body, SwingWriter writer) throws Exception {
        if (resources.getTemplate() == null) {
            return;
        }
        ComponentTemplateNode rootNode = resources.getTemplate().getRootNode();
        renderComponentTemplateNode(resources, rootNode, writer);
    }

    protected void renderBody(ComponentResources resources, List<ComponentTemplateNode> body, SwingWriter writer) throws Exception {
        for (ComponentTemplateNode bodyNode : body) {
            renderComponentTemplateNode(resources, bodyNode, writer);
        }
    }

    protected void renderComponentTemplateNode(ComponentResources resources, ComponentTemplateNode templateNode, SwingWriter writer) throws Exception {
        try {
            ComponentResources childComponent = componentFactory.create(resources, templateNode);
            int depthBefore = writer.depth();
            render(childComponent, templateNode.getComponents(), writer);
            int depthAfter = writer.depth();
            if (depthBefore != depthAfter) {
                String msg = String.format("SwingWriter depth is different before (%s) and after (%s) rendering", depthBefore, depthAfter);
                throw new LocationException(templateNode.getLocation(), msg);
            }
        } catch (LocationException e) {
            throw e;
        } catch (Exception e) {
            throw new LocationException(templateNode.getLocation(), e);
        }
    }
}
