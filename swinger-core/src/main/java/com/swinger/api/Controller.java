package com.swinger.api;

public interface Controller {
    boolean setupRender(ComponentInstance instance, SwingWriter writer) throws Exception;
    boolean beginRender(ComponentInstance instance, SwingWriter writer) throws Exception;
    boolean beforeRenderTemplate(ComponentInstance instance, SwingWriter writer) throws Exception;
    boolean afterRenderTemplate(ComponentInstance instance, SwingWriter writer) throws Exception;
    boolean beforeRenderBody(ComponentInstance instance, SwingWriter writer) throws Exception;
    boolean afterRenderBody(ComponentInstance instance, SwingWriter writer) throws Exception;
    boolean afterRender(ComponentInstance instance, SwingWriter writer) throws Exception;
    boolean cleanupRender(ComponentInstance instance, SwingWriter writer) throws Exception;
}
