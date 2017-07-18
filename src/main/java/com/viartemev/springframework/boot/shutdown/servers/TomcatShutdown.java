package com.viartemev.springframework.boot.shutdown.servers;

import com.viartemev.springframework.boot.shutdown.configuration.ShutdownProperties;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class TomcatShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(TomcatShutdown.class.getName());

    private volatile Connector connector;
    private final ShutdownProperties shutdownProperties;

    public TomcatShutdown(ShutdownProperties shutdownProperties) {
        this.shutdownProperties = shutdownProperties;
    }

    @Override
    public void customize(Connector connector) {
        this.connector = connector;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        LOG.info("Embedded Tomcat stopped accepting new requests");

        //see detail: https://tomcat.apache.org/tomcat-8.5-doc/config/http.html
        connector.setProperty("maxThreads", "0");
        connector.setProperty("acceptCount", "0");

        LOG.info("Starting shutdown process");
        Executor executor = connector.getProtocolHandler().getExecutor();
        if (executor instanceof ThreadPoolExecutor) {
            try {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                threadPoolExecutor.shutdown();
                while (!threadPoolExecutor.awaitTermination(shutdownProperties.getTimeout(), TimeUnit.MILLISECONDS)) {
                    LOG.warn("Waiting termination of threads...");
                }
            } catch (InterruptedException ex) {
                LOG.error("Tomcat termination error!", ex);
                Thread.currentThread().interrupt();
            }
            LOG.info("Shutdown process is completed successfully");
        }
    }
}
