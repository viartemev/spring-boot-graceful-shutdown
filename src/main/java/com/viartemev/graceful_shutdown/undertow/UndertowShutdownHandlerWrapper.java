package com.viartemev.graceful_shutdown.undertow;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;

public class UndertowShutdownHandlerWrapper implements HandlerWrapper {

    private GracefulShutdownHandler gracefulShutdownHandler;

    @Override
    public HttpHandler wrap(final HttpHandler handler) {
        if (gracefulShutdownHandler == null) {
            synchronized (this) {
                if (gracefulShutdownHandler == null) {
                    this.gracefulShutdownHandler = new GracefulShutdownHandler(handler);
                }
            }
        }
        return gracefulShutdownHandler;
    }

    public GracefulShutdownHandler getGracefulShutdownHandler() {
        return gracefulShutdownHandler;
    }

}
