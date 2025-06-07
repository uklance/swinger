package com.swinger.impl;

import com.swinger.api.*;
import com.swinger.sax.ComponentTemplateNode;
import com.swinger.sax.TemplateNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
        boolean test(Controller controller, SwingWriter writer) throws Exception;
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
        setupRender = new RenderNode("setupRender", Controller::setupRender);
        beginRender = new RenderNode("beginRender", Controller::beginRender);
        beforeRenderTemplate = new RenderNode("beforeRenderTemplate", Controller::beforeRenderTemplate, this::renderTemplate);
        beforeRenderBody = new RenderNode("beforeRenderBody", Controller::beforeRenderBody, this::renderBody);
        afterRenderBody = new RenderNode("afterRenderBody", Controller::afterRenderBody);
        afterRenderTemplate = new RenderNode("afterRenderTemplate", Controller::afterRenderTemplate);
        afterRender = new RenderNode("afterRender", Controller::afterRender);
        cleanupRender = new RenderNode("cleanupRender", Controller::cleanupRender);

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

    @Override
    public void render(ComponentResources resources, SwingWriter writer) throws Exception {
        render(resources, Collections.emptyList(), writer);
    }

    protected void render(ComponentResources resources, List<ComponentTemplateNode> body, SwingWriter writer) throws Exception {
        Controller controller = resources.getController();
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
        TemplateNode rootNode = resources.getTemplate().getRootNode();
        if (rootNode == null) {
            return;
        }
        if (rootNode.getType() != TemplateNode.Type.COMPONENT) {
            throw new RuntimeException(String.format("Expected COMPONENT, found %s", rootNode.getType()));
        }
        renderComponentTemplateNode(resources, (ComponentTemplateNode) rootNode, writer);
    }

    protected void renderBody(ComponentResources resources, List<ComponentTemplateNode> body, SwingWriter writer) throws Exception {
        for (ComponentTemplateNode bodyNode : body) {
            renderComponentTemplateNode(resources, bodyNode, writer);
        }
    }

    protected void renderComponentTemplateNode(ComponentResources resources, ComponentTemplateNode templateNode, SwingWriter writer) throws Exception {
        ComponentResources childComponent = componentFactory.create(templateNode.getName(), templateNode.getParameters(), templateNode.getComponents());
        render(childComponent, templateNode.getComponents(), writer);
    }
}
