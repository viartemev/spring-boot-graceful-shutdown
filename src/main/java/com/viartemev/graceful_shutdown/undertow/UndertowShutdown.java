package com.viartemev.graceful_shutdown.undertow;

import com.viartemev.graceful_shutdown.ShutdownProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

public class UndertowShutdown implements ApplicationListener<ContextClosedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(UndertowShutdown.class.getName());

    private final ShutdownProperties shutdownProperties;
    private final UndertowShutdownHandlerWrapper undertowShutdownHandlerWrapper;

    public UndertowShutdown(ShutdownProperties shutdownProperties, UndertowShutdownHandlerWrapper undertowShutdownHandlerWrapper) {
        this.shutdownProperties = shutdownProperties;
        this.undertowShutdownHandlerWrapper = undertowShutdownHandlerWrapper;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        //LOG.info("Embedded Undertow stopped accepting new requests");

        LOG.info("Starting shutdown process");
        try {
            undertowShutdownHandlerWrapper.getGracefulShutdownHandler().shutdown();
            while (!undertowShutdownHandlerWrapper.getGracefulShutdownHandler().awaitShutdown(shutdownProperties.getTimeout())) {
                LOG.warn("Waiting termination of threads...");
            }
        } catch (InterruptedException e) {
            LOG.error("Undertow termination error!", e);
            Thread.currentThread().interrupt();
        }
        LOG.info("Shutdown process is completed successfully");
    }

}
