package com.zjj.netdisk.utils;

import cn.hutool.core.date.DateUtil;
import java.sql.Timestamp;


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
        // System.currentTimeMillis() 获取当前UTC毫秒数
        // new Timestamp() 直接创建对象
        return new Timestamp(System.currentTimeMillis());
    }
}
