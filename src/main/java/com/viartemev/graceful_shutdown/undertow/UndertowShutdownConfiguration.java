package com.viartemev.graceful_shutdown.undertow;

import com.viartemev.graceful_shutdown.ShutdownProperties;
import io.undertow.Undertow;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

@Configuration
@EnableConfigurationProperties(ShutdownProperties.class)
@ConditionalOnClass({Servlet.class, Undertow.class})
@ConditionalOnBean(UndertowEmbeddedServletContainerFactory.class)
public class UndertowShutdownConfiguration {

    private ShutdownProperties shutdownProperties;

    public UndertowShutdownConfiguration(ShutdownProperties shutdownProperties) {
        this.shutdownProperties = shutdownProperties;
    }

    @Bean
    public UndertowShutdown undertowShutdown() {
        return new UndertowShutdown(shutdownProperties, shutdownWrapper());
    }

    @Bean
    public EmbeddedServletContainerCustomizer tomcatCustomizer() {
        return container -> {
            if (container instanceof UndertowEmbeddedServletContainerFactory) {
                ((UndertowEmbeddedServletContainerFactory) container).addDeploymentInfoCustomizers(undertowDeploymentShutdownCustomizer());
            }
        };
    }

    @Bean
    public UndertowDeploymentInfoCustomizer undertowDeploymentShutdownCustomizer() {
        return deploymentInfo -> deploymentInfo.addOuterHandlerChainWrapper(shutdownWrapper());
    }

    @Bean
    public UndertowShutdownHandlerWrapper shutdownWrapper() {
        return new UndertowShutdownHandlerWrapper();
    }

}
