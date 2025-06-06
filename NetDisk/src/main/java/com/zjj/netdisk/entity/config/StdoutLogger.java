package com.zjj.netdisk.entity.config;

/**
 * @author 34978
 */
public class StdoutLogger extends com.p6spy.engine.spy.appender.StdoutLogger {
    public void logText(String text) {
        System.err.println(text);
    }
}