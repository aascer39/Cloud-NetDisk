package com.zjj.netdisk;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import java.io.File;

public class MathCaptchaExample {

    public static void main(String[] args) {
        // 1. 创建扭曲验证码对象
        // 宽度200，高度100，字符数4（对MathGenerator无效），干扰线宽度4
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(200, 100, 4, 4);

        // 2. 设置生成器为数学计算生成器
        // 默认的加减乘除，例如 "2*3=?"
        captcha.setGenerator(new MathGenerator());

        // 3. 重新生成验证码（必须调用，否则内容还是随机字符）
        captcha.createCode();

        // 4. 获取验证码的计算结果
        // captcha.getCode() 方法获取的是数学表达式的 *正确答案*
        String correctAnswer = captcha.getCode();
        System.out.println("验证码图片上的问题对应的正确答案是：" + correctAnswer);

        // 5. 将验证码图片写入到文件
        String filePath = "math_captcha.png";
        captcha.write(new File(filePath));
        System.out.println("验证码图片已保存到: " + new File(filePath).getAbsolutePath());

        // --- 模拟用户验证过程 ---
        // 在Web应用中，`correctAnswer` 存入Session，用户提交的答案用来做比对

        // 模拟用户输入了正确答案
        boolean isCorrect = captcha.verify(correctAnswer);
        System.out.println("用户输入正确答案，校验结果: " + isCorrect);

        // 模拟用户输入了错误答案
        String wrongAnswer = "999";
        boolean isWrong = captcha.verify(wrongAnswer);
        System.out.println("用户输入错误答案 '"+ wrongAnswer +"'，校验结果: " + isWrong);
    }
}