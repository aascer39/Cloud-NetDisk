package com.zjj.netdisk.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;


/**
 * @author 34978
 */
@Slf4j
public class UtilityTools {
    private UtilityTools() {

    }

    /**
     * 获取当前北京时间对应的 java.sql.Timestamp 对象。
     * 这个对象可以直接用于 PreparedStatement.setTimestamp() 方法，
     * 以便存入数据库的 TIMESTAMP 类型的字段。
     *
     * @return 代表当前北京时间的 java.sql.Timestamp 对象
     */
    public static Timestamp getBeijingTimestamp() {
        // new Timestamp() 直接创建对象
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 计算 InputStream 内容的 SHA-256 哈希值。
     * <p>
     * <b>重要:</b> 此方法会完整消耗(读取)传入的 InputStream。
     * 如果后续还需要使用原始数据流，调用方必须重新获取一个新的 InputStream。
     * </p>
     *
     * @param inputStream 要计算哈希的输入流。
     * @return 表示文件内容的 SHA-256 哈希值的十六进制字符串。
     * @throws NoSuchAlgorithmException 如果系统中不支持 SHA-256 算法。
     * @throws IOException 如果在读取输入流时发生 I/O 错误。
     */
    public static String getFileHash(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null.");
        }

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

        // DigestInputStream 会在读取流的同时更新 MessageDigest
        // 使用 try-with-resources 确保 DigestInputStream 被正确关闭
        try (DigestInputStream dis = new DigestInputStream(inputStream, sha256)) {
            // 消耗整个流以完成哈希计算。
            StreamUtils.copy(dis, OutputStream.nullOutputStream());
        }

        // 当流被完全读取并且 DigestInputStream 关闭后，从 MessageDigest 实例获取哈希值
        byte[] hashBytes = sha256.digest();
        // 使用你项目中的字节转十六进制方法
        return UtilityTools.bytesToHexString(hashBytes);
    }


    /**
     * 将字节数组转换为十六进制字符串。
     *
     * @param bytes 要转换的字节数组
     * @return 对应的十六进制字符串
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            // 获取字节的十六进制表示
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                // 如果长度为1，前面补0，确保两位表示
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


}
