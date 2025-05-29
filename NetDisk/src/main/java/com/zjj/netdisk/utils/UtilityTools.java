package com.zjj.netdisk.utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author 34978
 */
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
        // 1. 获取当前的UTC时间点
        Instant nowUtc = Instant.now();

        // 2. 定义北京时区
        ZoneId beijingZone = ZoneId.of("Asia/Shanghai");

        // 3. 将UTC时间点转换为北京时区的ZonedDateTime
        ZonedDateTime beijingZonedDateTime = nowUtc.atZone(beijingZone);

        // 4. 将ZonedDateTime转换为LocalDateTime (java.sql.Timestamp.valueOf需要LocalDateTime)
        LocalDateTime beijingLocalDateTime = beijingZonedDateTime.toLocalDateTime();

        // 5. 使用LocalDateTime创建java.sql.Timestamp对象
        return Timestamp.valueOf(beijingLocalDateTime);
    }
}
