package com.example.zhijingai.demo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * jwt相关配置参数（admin-secret-key、admin-ttl、admin-token-name）的封装类
 */
// 声明为组件，能够被springboot自动装配到其他需要的地方
@Component
// 扫描yml配置文件的sky.jwt，让封装类属性与其对应
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {
    /**
     * 生成jwt令牌相关配置
     */
    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;

}
