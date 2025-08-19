package com.example.exception.starter.enums;

/**
 * 错误码枚举
 */
public enum ErrorCode {
    
    // 系统级别错误 (1000-1999)
    SUCCESS(0, "操作成功"),
    SYSTEM_ERROR(1000, "系统内部错误"),
    PARAM_ERROR(1001, "参数错误"),
    VALIDATION_ERROR(1002, "数据校验失败"),
    REQUEST_METHOD_NOT_SUPPORTED(1003, "请求方法不支持"),
    MEDIA_TYPE_NOT_SUPPORTED(1004, "媒体类型不支持"),
    MISSING_REQUEST_PARAMETER(1005, "缺少请求参数"),
    TYPE_MISMATCH(1006, "参数类型不匹配"),
    HTTP_MESSAGE_NOT_READABLE(1007, "请求体不可读"),
    MISSING_SERVLET_REQUEST_PART(1008, "缺少文件上传参数"),
    MAX_UPLOAD_SIZE_EXCEEDED(1009, "文件上传大小超限"),
    
    // 业务级别错误 (2000-2999)
    BUSINESS_ERROR(2000, "业务处理失败"),
    RESOURCE_NOT_FOUND(2001, "资源不存在"),
    RESOURCE_ALREADY_EXISTS(2002, "资源已存在"),
    OPERATION_NOT_ALLOWED(2003, "操作不被允许"),
    
    // 权限相关错误 (3000-3999)
    UNAUTHORIZED(3000, "未授权访问"),
    ACCESS_DENIED(3001, "访问被拒绝"),
    TOKEN_EXPIRED(3002, "令牌已过期"),
    TOKEN_INVALID(3003, "令牌无效"),
    
    // 外部服务错误 (4000-4999)
    EXTERNAL_SERVICE_ERROR(4000, "外部服务错误"),
    SERVICE_UNAVAILABLE(4001, "服务不可用"),
    TIMEOUT_ERROR(4002, "请求超时");
    
    private final int code;
    private final String message;
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
