package com.zjj.netdisk;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;

import java.lang.reflect.Type;

public class testTools {
    public static void main(String[] args) {
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(200, 45, 4, 4);
    // 自定义验证码内容为四则运算方式
        captcha.setGenerator(new MathGenerator());
    // 重新生成code
        captcha.createCode();
    }
}
