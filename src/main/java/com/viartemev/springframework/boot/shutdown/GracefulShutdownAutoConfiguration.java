package com.viartemev.springframework.boot.shutdown;

import com.viartemev.springframework.boot.shutdown.configuration.ShutdownProperties;
import com.viartemev.springframework.boot.shutdown.servers.TomcatShutdown;
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
public class GracefulShutdownAutoConfiguration {

    @Configuration
    @ConditionalOnClass({Servlet.class, Tomcat.class})
    @ConditionalOnBean(TomcatEmbeddedServletContainerFactory.class)
    class EmbeddedTomcat {

        @Bean
        public TomcatShutdown tomcatShutdown(ShutdownProperties shutdownProperties) {
            return new TomcatShutdown(shutdownProperties);
        }

        @Bean
        public EmbeddedServletContainerCustomizer tomcatCustomizer(ShutdownProperties shutdownProperties) {
            return container -> {
                if (container instanceof TomcatEmbeddedServletContainerFactory) {
                    ((TomcatEmbeddedServletContainerFactory) container)
                            .addConnectorCustomizers(tomcatShutdown(shutdownProperties));
                }
            };
        }
    }
}
