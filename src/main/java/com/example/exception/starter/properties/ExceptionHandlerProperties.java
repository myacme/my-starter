package com.example.exception.starter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异常处理器配置属性
 */
@ConfigurationProperties(prefix = "exception.handler")
public class ExceptionHandlerProperties {
    
    /**
     * 是否启用全局异常处理器
     */
    private boolean enabled = true;
    
    /**
     * 是否启用异常日志记录
     */
    private boolean enableLogging = true;
    
    /**
     * 是否在响应中包含异常堆栈信息
     */
    private boolean includeStackTrace = false;
    
    /**
     * 异常堆栈信息最大行数
     */
    private int maxStackTraceLines = 50;
    
    /**
     * 是否启用字段验证错误详情
     */
    private boolean includeFieldErrors = true;
    
    /**
     * 异常日志级别配置
     */
    private LogLevel logLevel = new LogLevel();
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnableLogging() {
        return enableLogging;
    }
    
    public void setEnableLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }
    
    public boolean isIncludeStackTrace() {
        return includeStackTrace;
    }
    
    public void setIncludeStackTrace(boolean includeStackTrace) {
        this.includeStackTrace = includeStackTrace;
    }
    
    public int getMaxStackTraceLines() {
        return maxStackTraceLines;
    }
    
    public void setMaxStackTraceLines(int maxStackTraceLines) {
        this.maxStackTraceLines = maxStackTraceLines;
    }
    
    public boolean isIncludeFieldErrors() {
        return includeFieldErrors;
    }
    
    public void setIncludeFieldErrors(boolean includeFieldErrors) {
        this.includeFieldErrors = includeFieldErrors;
    }
    
    public LogLevel getLogLevel() {
        return logLevel;
    }
    
    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }
    
    /**
     * 日志级别配置
     */
    public static class LogLevel {
        
        /**
         * 业务异常日志级别 (TRACE, DEBUG, INFO, WARN, ERROR)
         */
        private String business = "WARN";
        
        /**
         * 系统异常日志级别 (TRACE, DEBUG, INFO, WARN, ERROR)
         */
        private String system = "ERROR";
        
        /**
         * 参数校验异常日志级别 (TRACE, DEBUG, INFO, WARN, ERROR)
         */
        private String validation = "WARN";
        
        public String getBusiness() {
            return business;
        }
        
        public void setBusiness(String business) {
            this.business = business;
        }
        
        public String getSystem() {
            return system;
        }
        
        public void setSystem(String system) {
            this.system = system;
        }
        
        public String getValidation() {
            return validation;
        }
        
        public void setValidation(String validation) {
            this.validation = validation;
        }
    }
}
