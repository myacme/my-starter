package com.example.exception.starter.exception;

import com.example.exception.starter.enums.ErrorCode;

/**
 * 数据校验异常类
 */
public class ValidationException extends BaseException {
    
    public ValidationException(String message) {
        super(ErrorCode.VALIDATION_ERROR, message);
    }
    
    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public ValidationException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
    
    public ValidationException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
    
    public ValidationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
