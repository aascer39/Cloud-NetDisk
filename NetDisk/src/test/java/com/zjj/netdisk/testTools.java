package com.zjj.netdisk;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;

import java.lang.reflect.Type;

public class testTools {
    public static void main(String[] args) {
        QrCodeUtil.generate(
                "https://ys-api.mihoyo.com/event/download_porter/link/ys_cn/official/android_default", //二维码内容
                QrConfig.create().setImg("D:\\DownLoad\\BrowserDownload\\初音.jpg"), //附带logo
                FileUtil.file("D:\\Projects\\Network Disk\\ND frontend\\NDfrontend\\public\\qr.jpg")//写出到的文件
        );
    }
}
