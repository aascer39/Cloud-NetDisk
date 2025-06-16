package com.zjj.netdisk.satoken;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 配置类，用于确保自定义 StpKit 被加载
 *
 * @author 34978
 */
@Configuration
public class SaTokenBeanConfiguration implements InitializingBean {

    /**
     * Spring Bean 初始化后执行此方法
     * 在这里通过 Class.forName() 主动加载 StpKit，
     * 从而确保 StpLogic 被正确地创建和注册。
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 确保 StpKit 被 JVM 加载
        Class.forName("com.zjj.netdisk.satoken.StpKit");
    }
}