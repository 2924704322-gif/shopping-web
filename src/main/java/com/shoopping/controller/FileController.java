package com.shoopping.controller;

import com.shoopping.common.Result;
import com.shoopping.config.UploadProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 *
 * @author shopping-team
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileController {

    private final UploadProperties uploadProperties;

    /**
     * 上传图片
     * 存储路径: uploads/yyyy/MM/dd/uuid.ext
     * 返回 URL: /uploads/yyyy/MM/dd/uuid.ext
     */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        // 1. 校验文件是否为空
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }

        // 2. 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !uploadProperties.getAllowedTypes().contains(contentType)) {
            return Result.error(400, "不支持的文件类型，仅允许: "
                    + String.join(", ", uploadProperties.getAllowedTypes()));
        }

        // 3. 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 4. 生成存储路径: uploads/yyyy/MM/dd/
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString() + extension;
        String relativePath = dateDir + "/" + fileName;

        try {
            // 5. 解析上传目录为绝对路径并保存文件
            Path basePath = Paths.get(uploadProperties.getPath()).toAbsolutePath().normalize();
            Path targetDir = basePath.resolve(dateDir);
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(fileName);
            file.transferTo(targetFile.toFile());

            log.info("文件上传成功: {} -> {}", originalFilename, relativePath);

            // 6. 返回可访问 URL
            Map<String, String> result = new HashMap<>();
            result.put("url", "/uploads/" + relativePath);
            result.put("fileName", fileName);
            result.put("originalName", originalFilename);

            return Result.success("上传成功", result);

        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            return Result.error(500, "文件上传失败，请稍后重试");
        }
    }
}
