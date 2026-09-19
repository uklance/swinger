package com.swinger.api;

public interface Controller {
    boolean setupRender(Object instance, SwingWriter writer) throws Exception;
    boolean beginRender(Object instance, SwingWriter writer) throws Exception;
    boolean beforeRenderTemplate(Object instance, SwingWriter writer);
    boolean beforeRenderBody(Object instance, SwingWriter writer) throws Exception;
    boolean afterRenderBody(Object instance, SwingWriter writer) throws Exception;
    boolean afterRenderTemplate(Object instance, SwingWriter writer) throws Exception;
    boolean afterRender(Object instance, SwingWriter writer) throws Exception;
    boolean cleanupRender(Object instance, SwingWriter writer) throws Exception;
}
