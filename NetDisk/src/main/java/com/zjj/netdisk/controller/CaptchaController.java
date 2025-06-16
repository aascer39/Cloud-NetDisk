package com.zjj.netdisk.controller;

import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 34978
 */
@RestController
public class CaptchaController {

    public static final String CAPTCHA_KEY = "captcha_code";

    /**
     * 获取验证码图片的接口
     * @param request HttpServletRequest，用于获取 Session
     * @return 返回一个包含 Base64 图片字符串的 Map
     */
    @GetMapping("/api/captcha")
    public Map<String, String> getCaptcha(HttpServletRequest request) {
        // 1. 使用 Hutool 创建数学验证码
        ShearCaptcha captcha = new ShearCaptcha(200, 45, 4, 4);
        captcha.setGenerator(new MathGenerator());
        captcha.createCode();

        // 2. 获取验证码的正确答案
        String correctAnswer = captcha.getCode();
        
        // 3. 将正确答案存储到 Session 中
        HttpSession session = request.getSession();
        session.setAttribute(CAPTCHA_KEY, correctAnswer);
        System.out.println("当前 Session ID: " + session.getId() + "，生成的验证码答案是: " + correctAnswer);

        // 4. 获取图片的 Base64 编码
        // captcha.getImageBase64() 返回的是不带 "data:image/png;base64," 前缀的纯编码
        String imageBase64 = captcha.getImageBase64();
        String captchaImage = "data:image/png;base64," + imageBase64;

        // 5. 将 Base64 字符串返回给前端
        Map<String, String> response = new HashMap<>();
        response.put("captchaImage", captchaImage);
        
        return response;
    }

    /**
     * 校验用户输入的验证码
     * @param userInputCode 用户输入的答案
     * @param request HttpServletRequest，用于获取 Session 中存储的正确答案
     * @return 校验结果
     */
    @PostMapping("/api/verify")
    public Map<String, Object> verifyCaptcha(@RequestParam String userInputCode, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String sessionCode = (String) session.getAttribute(CAPTCHA_KEY);

        Map<String, Object> response = new HashMap<>();
        
        // 从 Session 中移除验证码，确保一次性有效
        session.removeAttribute(CAPTCHA_KEY);

        if (sessionCode == null) {
            response.put("success", false);
            response.put("message", "验证码已过期，请刷新");
            return response;
        }

        if (userInputCode == null || userInputCode.isEmpty()) {
            response.put("success", false);
            response.put("message", "请输入验证码");
            return response;
        }

        if (sessionCode.equalsIgnoreCase(userInputCode)) {
            response.put("success", true);
            response.put("message", "验证成功！");
        } else {
            response.put("success", false);
            response.put("message", "验证码错误！");
        }
        
        return response;
    }
}