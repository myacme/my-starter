package com.example.exception.starter.autoconfigure;

import com.example.exception.starter.handler.GlobalExceptionHandler;
import com.example.exception.starter.properties.ExceptionHandlerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 全局异常处理器自动配置类
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(DispatcherServlet.class)
@ConditionalOnProperty(prefix = "exception.handler", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ExceptionHandlerProperties.class)
public class ExceptionHandlerAutoConfiguration {
    
    /**
     * 注册全局异常处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler(ExceptionHandlerProperties properties) {
        return new GlobalExceptionHandler(properties);
    }
}
