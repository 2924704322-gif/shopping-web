package com.shoopping.controller.admin;

import com.shoopping.common.Result;
import com.shoopping.entity.Product;
import com.shoopping.service.ProductService;
import com.shoopping.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮件测试控制器（管理员）
 *
 * @author shopping-team
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/email")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEmailController {

    private final JavaMailSender mailSender;
    private final UserService userService;
    private final ProductService productService;

    @Value("${MAIL_REPORT_TO:admin@example.com}")
    private String reportTo;

    /** 预览报表内容（不发送） */
    @GetMapping("/preview")
    public Result<java.util.Map<String, Object>> preview() {
        return Result.success(java.util.Map.of(
            "to", reportTo,
            "subject", "【购物平台】每日数据报表",
            "content", generateReport()
        ));
    }

    private String generateReport() {
        return String.format("""
                购物平台每日数据报表
                ====================
                统计时间: %s

                一、用户数据
                  注册用户总数: %d

                二、商品数据
                  商品总数: %d
                  上架商品数: %d
                  下架商品数: %d

                此邮件由系统自动发送，请勿回复。
                """,
                java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                userService.count(),
                productService.count(),
                productService.lambdaQuery().eq(Product::getStatus, 1).count(),
                productService.count() - productService.lambdaQuery().eq(Product::getStatus, 1).count());
    }

    /** 手动触发日报发送 */
    @PostMapping("/send-report")
    public Result<String> sendReport() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2924704322@qq.com");
            message.setTo(reportTo);
            message.setSubject("【购物平台】每日数据报表");
            message.setText(generateReport());
            mailSender.send(message);
            log.info("邮件发送成功 -> {}", reportTo);
            return Result.success("邮件已发送至 " + reportTo);
        } catch (Exception e) {
            log.error("邮件发送失败: {}", e.getMessage(), e);
            return Result.error(500, "邮件发送失败: " + e.getMessage());
        }
    }
}
