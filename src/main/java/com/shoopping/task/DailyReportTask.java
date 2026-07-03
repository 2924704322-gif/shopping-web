package com.shoopping.task;

import com.shoopping.entity.Product;
import com.shoopping.service.ProductService;
import com.shoopping.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每日报表定时任务
 * 每天早上 8:00 发送平台数据统计邮件
 *
 * @author shopping-team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportTask {

    private final JavaMailSender mailSender;
    private final UserService userService;
    private final ProductService productService;

    /** 收件人，可配置多个 */
    @Value("${MAIL_REPORT_TO:admin@example.com}")
    private String reportTo;

    /**
     * 每日 8:00 发送报表
     * cron: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyReport() {
        try {
            long userCount = userService.count();
            long productCount = productService.count();
            long onShelfCount = productService.lambdaQuery()
                    .eq(Product::getStatus, 1).count();

            String content = buildReportContent(userCount, productCount, onShelfCount);
            sendEmail(reportTo, "【购物平台】每日数据报表", content);

            log.info("每日报表邮件已发送至: {}", reportTo);
        } catch (Exception e) {
            log.error("每日报表发送失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 构建报表内容
     */
    private String buildReportContent(long userCount, long productCount, long onShelfCount) {
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
                userCount, productCount, onShelfCount, productCount - onShelfCount);
    }

    /**
     * 发送简单文本邮件
     */
    private void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("2924704322@qq.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
