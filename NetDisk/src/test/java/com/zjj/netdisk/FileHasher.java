package com.zjj.netdisk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileHasher {

    /**
     * 计算指定文件的 SHA-256 哈希值。
     *
     * @param file 要计算哈希值的文件对象
     * @return 文件的 SHA-256 哈希值 (十六进制字符串)，如果发生错误则返回 null。
     */
    public static String getFileSHA256(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            System.err.println("错误：文件为空、不存在或不是一个有效的文件。");
            return null;
        }

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 是标准算法，此异常理论上不应发生
            System.err.println("错误：无法获取 SHA-256 算法实例。" + e.getMessage());
            // 在实际应用中，可能需要向上抛出异常或进行更复杂的错误处理
            return null;
        }

        // 使用 try-with-resources 确保 FileInputStream 自动关闭
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8192]; // 创建一个缓冲区来读取文件内容
            int bytesRead;
            // 逐块读取文件内容，并更新 MessageDigest
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.err.println("错误：读取文件时发生 IO 异常。" + e.getMessage());
            return null;
        }

        // 完成哈希计算，获取字节数组形式的哈希值
        byte[] hashedBytes = digest.digest();

        // 将字节数组转换为十六进制字符串
        return bytesToHexString(hashedBytes);
    }

    /**
     * 计算指定文件路径的 SHA-256 哈希值。
     *
     * @param filePath 文件的完整路径
     * @return 文件的 SHA-256 哈希值 (十六进制字符串)，如果发生错误则返回 null。
     */
    public static String getFileSHA256(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("错误：文件路径不能为空。");
            return null;
        }
        return getFileSHA256(new File(filePath));
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
            String hex = Integer.toHexString(0xff & b); // 获取字节的十六进制表示
            if (hex.length() == 1) {
                hexString.append('0'); // 如果长度为1，前面补0，确保两位表示
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // --- 使用示例 ---
    public static void main(String[] args) {
        // 假设在项目根目录下有一个名为 "testfile.txt" 的文件
        // 你需要先创建这个文件并写入一些内容才能测试
        String filePath = "D:\\DownLoad\\BrowserDownload\\MinIOTest\\图片\\初风.jpg"; // 替换为你的文件路径
        File testFile = new File(filePath);

        // 为了测试，我们先创建一个简单的文件
        try {
            if (!testFile.exists()) {
                java.nio.file.Files.write(testFile.toPath(), "Hello, SHA-256 world!".getBytes());
                System.out.println("测试文件 '" + filePath + "' 已创建。");
            }
        } catch (IOException e) {
            System.err.println("创建测试文件失败: " + e.getMessage());
            return;
        }

        String sha256Hash = getFileSHA256(testFile);

        if (sha256Hash != null) {
            System.out.println("文件 '" + testFile.getName() + "' 的 SHA-256 哈希值是:");
            System.out.println(sha256Hash);
            System.out.println("哈希值长度: " + sha256Hash.length()); // SHA-256 的十六进制字符串长度为64
        } else {
            System.out.println("未能计算文件 '" + testFile.getName() + "' 的 SHA-256 哈希值。");
        }

        // 测试通过文件路径获取哈希
        String sha256HashFromPath = getFileSHA256(filePath);
        if (sha256HashFromPath != null) {
            System.out.println("\n通过路径获取文件 '" + filePath + "' 的 SHA-256 哈希值是:");
            System.out.println(sha256HashFromPath);
        }
    }
}