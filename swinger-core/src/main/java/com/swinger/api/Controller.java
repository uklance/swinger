package com.swinger.api;

public interface Controller {
    default boolean setupRender(SwingWriter writer) throws Exception {
        return true;
    }

    default boolean beginRender(SwingWriter writer) throws Exception {
        return true;
    }

    default boolean beforeRenderTemplate(SwingWriter writer) throws Exception {
        return true;
    }

    default boolean beforeRenderBody(SwingWriter writer) throws Exception {
        return true;
    }

    default boolean afterRenderBody(SwingWriter writer) throws Exception {
        return true;
    }

    default boolean afterRenderTemplate(SwingWriter writer) throws Exception {
        return true;
    }

    default boolean afterRender(SwingWriter writer) throws Exception {
        return true;
    }

    default boolean cleanupRender(SwingWriter writer) throws Exception {
        return true;
    }
}
