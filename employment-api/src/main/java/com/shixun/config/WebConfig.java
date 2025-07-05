package com.shixun.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {


//    @Autowired
//    private SqlLogInterceptor sqlLogInterceptor;
    // 跨域配置
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有路径
                .allowedOrigins("*") // 所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    // 静态资源访问（可选）
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }
//    @Bean
//    public ConfigurationCustomizer configurationCustomizer() {
//        log.info("SqlLogInterceptor已注册到MyBatis配置");
//        return configuration -> configuration.addInterceptor(sqlLogInterceptor);
//    }

    // 拦截器（如有登录验证需求）
    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
    //     registry.addInterceptor(new LoginInterceptor())
    //             .addPathPatterns("/**")
    //             .excludePathPatterns("/login", "/error");
    // }
}
