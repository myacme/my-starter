package com.example.exception.starter.exception;

import com.example.exception.starter.enums.ErrorCode;

/**
 * 业务异常类
 */
public class BusinessException extends BaseException {
    
    public BusinessException(String message) {
        super(ErrorCode.BUSINESS_ERROR, message);
    }
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
    
    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
    
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
