package com.viartemev.graceful_shutdown.tomcat;

import com.viartemev.graceful_shutdown.ShutdownProperties;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

@Configuration
@EnableConfigurationProperties(ShutdownProperties.class)
@ConditionalOnClass({Servlet.class, Tomcat.class})
@ConditionalOnBean(TomcatEmbeddedServletContainerFactory.class)
public class TomcatShutdownConfiguration {

    private ShutdownProperties shutdownProperties;

    public TomcatShutdownConfiguration(ShutdownProperties shutdownProperties) {
        this.shutdownProperties = shutdownProperties;
    }

    @Bean
    public TomcatShutdown tomcatShutdown() {
        return new TomcatShutdown(shutdownProperties);
    }

    @Bean
    public EmbeddedServletContainerCustomizer tomcatCustomizer() {
        return container -> {
            if (container instanceof TomcatEmbeddedServletContainerFactory) {
                ((TomcatEmbeddedServletContainerFactory) container)
                        .addConnectorCustomizers(tomcatShutdown());
            }
        };
    }

}
