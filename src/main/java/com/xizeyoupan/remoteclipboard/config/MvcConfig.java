package com.xizeyoupan.remoteclipboard.config;

import com.xizeyoupan.remoteclipboard.interceptors.ConnectionInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    final ApplicationContext applicationContext;

    public MvcConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(applicationContext.getBean("connectionInterceptor", ConnectionInterceptor.class))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/v1/user", "/api/v1/user/add", "/error");
    }
}
