package com.zjj.netdisk.utils;

import lombok.*;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail; // 导入 HtmlEmail
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 34978
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class EmailHelper {
    private static final Logger log = LoggerFactory.getLogger(EmailHelper.class);

    @Value("${spring.mail.username}")
    private String senderEmailAddr;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${netdisk.mail.display-name}")
    private String displayName;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    /**
     * 发送美化后的HTML邮件
     *
     * @param subject          主题
     * @param htmlContent      HTML 格式的邮件内容
     * @param receiveEmailAddr 收件人地址
     */
    @Async
    public void sendHtmlEmail(String subject, String htmlContent, String receiveEmailAddr) {
        try {
            // 1. 创建 HtmlEmail 实例
            HtmlEmail email = new HtmlEmail();

            // 2. 配置服务器信息 (与之前相同)
            email.setHostName(host);
            email.setSmtpPort(port);
            email.setAuthenticator(new DefaultAuthenticator(senderEmailAddr, password));
            email.setSSLOnConnect(true);
            // 设置字符集，防止中文乱码
            email.setCharset("UTF-8");

            // 3. 配置邮件基本信息
            email.setFrom(senderEmailAddr, displayName);
            email.addTo(receiveEmailAddr);
            email.setSubject(subject);
            email.setSentDate(new Date());

            // 4. 设置邮件内容
            // 设置HTML内容
            email.setHtmlMsg(htmlContent);
            // 设置纯文本备用内容
            email.setTextMsg("您的邮箱客户端不支持HTML格式，请使用支持HTML的客户端查看。");

            // 5. 发送
            email.send();
            log.info("HTML邮件发送成功: 主题为'{}'的邮件已发送至 {}", subject, receiveEmailAddr);

        } catch (EmailException e) {
            log.error("HTML邮件发送失败: {}", e.getMessage(), e);
            throw new RuntimeException("HTML邮件发送失败", e);
        }
    }
}