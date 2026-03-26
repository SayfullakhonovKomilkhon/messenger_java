package com.messenger.common.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final GroupRoleInterceptor groupRoleInterceptor;

    public WebMvcConfig(GroupRoleInterceptor groupRoleInterceptor) {
        this.groupRoleInterceptor = groupRoleInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(groupRoleInterceptor)
                .addPathPatterns("/api/v1/groups/**");
    }
}
