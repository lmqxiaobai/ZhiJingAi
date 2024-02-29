package com.example.zhijingai.config;

import com.example.zhijingai.demo.interceptor.JwtTokenAdminInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * 配置类，注册web层相关的组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    // 去依赖注入jwt令牌校验的拦截器,返回ture通过，返回false不通过
    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

//    /**
//     * 注册自定义拦截器
//     *
//     * @param registry
//     */
//    protected void addInterceptors(InterceptorRegistry registry){
//        log.info("开始注册自定义拦截器...");
//        registry.addInterceptor(jwtTokenAdminInterceptor)
//                .addPathPatterns("/api/**")
//                .excludePathPatterns("/static/**")
//                .excludePathPatterns("/user/login");
//    }
//
//
    /**
     * 设置不被拦截的静态资源映射
     * @param registry
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始设置静态资源映射...");
        // 对静态页面放行
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        // 可以登录这个swagger静态页面，然后自动去生成"com.example.zhijingai.controller"下的接口，并进行测试
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 通过knife4j生成接口文档
     * @return
     */
    @Bean
    public Docket docket() {
        log.info("准备生成接口文档...");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("智“境”AI策划师项目接口文档")
                .version("2.0")
                .description("智“境”AI策划师项目接口文档")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                // 去扫描"com.example.zhijingai.controller"包下的内容，并对应生成相应的接口
                .apis(RequestHandlerSelectors.basePackage("com.example.zhijingai.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

}
