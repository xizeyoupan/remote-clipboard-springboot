package com.xizeyoupan.remoteclipboard.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@Slf4j
public class CorsConfig {
    @Value("${app.origin}")
    private String origin;

    @Value("${spring.profiles.active}")
    private String deployMode;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(origin); // 1允许任何域名使用
        corsConfiguration.addAllowedHeader("*"); // 2允许任何头
        corsConfiguration.addAllowedMethod("*"); // 3允许任何方法（post、get等）
        if (deployMode.equals("dev")) {
            corsConfiguration.setAllowCredentials(true);
        }
        source.registerCorsConfiguration("/**", corsConfiguration);// 4处理所有请求的跨域配置
        return new CorsFilter(source);
    }
}