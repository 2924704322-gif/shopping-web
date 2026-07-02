package com.shoopping.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 文件上传配置属性
 *
 * @author shopping-team
 */
@Data
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {

    /** 上传文件存储路径 */
    private String path = "uploads";

    /** 允许的文件类型 */
    private List<String> allowedTypes = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");
}
