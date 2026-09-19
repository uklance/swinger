package com.swinger.impl;

import com.swinger.LocationException;
import com.swinger.annotation.*;
import com.swinger.api.*;
import com.swinger.sax.ComponentTemplateNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        boolean test(Object instance, SwingWriter writer) throws Exception;
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
        setupRender = new RenderNode("setupRender", (instance, writer) -> invokeLifecycle(instance, SetupRender.class, writer,
                Controller::setupRender));
        beginRender = new RenderNode("beginRender", (instance, writer) -> invokeLifecycle(instance, BeginRender.class, writer,
                Controller::beginRender));
        beforeRenderTemplate = new RenderNode("beforeRenderTemplate", (instance, writer) -> invokeLifecycle(instance, BeforeRenderTemplate.class, writer,
                Controller::beforeRenderTemplate), this::renderTemplate);
        beforeRenderBody = new RenderNode("beforeRenderBody", (instance, writer) -> invokeLifecycle(instance, BeforeRenderBody.class, writer,
                Controller::beforeRenderBody), this::renderBody);
        afterRenderBody = new RenderNode("afterRenderBody", (instance, writer) -> invokeLifecycle(instance, AfterRenderBody.class, writer,
                Controller::afterRenderBody));
        afterRenderTemplate = new RenderNode("afterRenderTemplate", (instance, writer) -> invokeLifecycle(instance, AfterRenderTemplate.class, writer,
                Controller::afterRenderTemplate));
        afterRender = new RenderNode("afterRender", (instance, writer) -> invokeLifecycle(instance, AfterRender.class, writer,
                Controller::afterRender));
        cleanupRender = new RenderNode("cleanupRender", (instance, writer) -> invokeLifecycle(instance, CleanupRender.class, writer,
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
                                    ControllerLifecycleFallback fallback) throws Exception {
        boolean found = false;
        boolean proceed = true;
        for (Class<?> currentType = instance.getClass(); currentType != null; currentType = currentType.getSuperclass()) {
            for (Method method : currentType.getDeclaredMethods()) {
                if (method.getAnnotation(annotation) != null) {
                    found = true;
                    proceed &= invokeAnnotatedMethod(method, instance, writer);
                }
            }
        }
        if (!found && instance instanceof Controller) {
            proceed = fallback.invoke((Controller) instance, instance, writer);
        }
        return proceed;
    }

    private boolean invokeAnnotatedMethod(Method method, Object instance, SwingWriter writer) throws Exception {
        Object result;
        try {
            method.setAccessible(true);
            if (method.getParameterCount() == 0) {
                result = method.invoke(instance);
            } else if (method.getParameterCount() == 1 && SwingWriter.class.isAssignableFrom(method.getParameterTypes()[0])) {
                result = method.invoke(instance, writer);
            } else {
                throw new IllegalArgumentException("Lifecycle method must accept no parameters or a SwingWriter: " + method);
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                throw (Exception) cause;
            }
            throw e;
        }
        if (method.getReturnType() == void.class) {
            return true;
        }
        if (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class) {
            return Boolean.TRUE.equals(result);
        }
        throw new IllegalArgumentException("Lifecycle method must return boolean or void: " + method);
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
        Object controller = resources.getController();
        RenderNode currentNode = setupRender;
        while (currentNode != null) {
            boolean proceed = currentNode.getPredicate().test(controller, writer);
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
