package com.example.exception.starter.handler;

import com.example.exception.starter.enums.ErrorCode;
import com.example.exception.starter.exception.BaseException;
import com.example.exception.starter.model.ErrorResponse;
import com.example.exception.starter.properties.ExceptionHandlerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 全局异常处理器
 * @author MyAcme
 */
@RestControllerAdvice
@ConditionalOnProperty(prefix = "exception.handler", name = "enabled", havingValue = "true", matchIfMissing = true)
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private final ExceptionHandlerProperties properties;
    
    public GlobalExceptionHandler(ExceptionHandlerProperties properties) {
        this.properties = properties;
    }
    
    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        ErrorResponse response = new ErrorResponse(ex.getErrorCode().getCode(), ex.getMessage(), path);
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "Business exception occurred", request);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    /**
     * 处理参数校验异常 - @Valid注解校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        String path = request.getRequestURI();
        ErrorResponse response = new ErrorResponse(ErrorCode.VALIDATION_ERROR.getCode(), 
                ErrorCode.VALIDATION_ERROR.getMessage(), path);
        
        response.setFieldErrors(buildFieldErrors(ex.getBindingResult()));
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "Method argument validation failed", request);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        ErrorResponse response = new ErrorResponse(ErrorCode.VALIDATION_ERROR.getCode(), 
                ErrorCode.VALIDATION_ERROR.getMessage(), path);
        
        response.setFieldErrors(buildFieldErrors(ex.getBindingResult()));
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "Parameter binding failed", request);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理约束违反异常 - @Validated注解校验失败
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        String path = request.getRequestURI();
        ErrorResponse response = new ErrorResponse(ErrorCode.VALIDATION_ERROR.getCode(), 
                ErrorCode.VALIDATION_ERROR.getMessage(), path);
        
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            Object invalidValue = violation.getInvalidValue();
            fieldErrors.add(new ErrorResponse.FieldError(propertyPath, invalidValue, message));
        }
        response.setFieldErrors(fieldErrors);
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "Constraint validation failed", request);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        String path = request.getRequestURI();
        String message = String.format("请求方法 '%s' 不被支持，支持的方法: %s", 
                ex.getMethod(), String.join(", ", ex.getSupportedMethods()));
        
        ErrorResponse response = new ErrorResponse(ErrorCode.REQUEST_METHOD_NOT_SUPPORTED.getCode(), 
                message, path);
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "HTTP request method not supported", request);
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }
    
    /**
     * 处理媒体类型不支持异常
     */
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class, HttpMediaTypeNotAcceptableException.class})
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeException(Exception ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        ErrorResponse response = new ErrorResponse(ErrorCode.MEDIA_TYPE_NOT_SUPPORTED.getCode(), 
                ErrorCode.MEDIA_TYPE_NOT_SUPPORTED.getMessage(), path);
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "HTTP media type not supported", request);
        
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }
    
    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        String path = request.getRequestURI();
        String message = String.format("缺少必需的请求参数: %s", ex.getParameterName());
        
        ErrorResponse response = new ErrorResponse(ErrorCode.MISSING_REQUEST_PARAMETER.getCode(), 
                message, path);
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "Missing servlet request parameter", request);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler({TypeMismatchException.class, ConversionNotSupportedException.class})
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(Exception ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        ErrorResponse response = new ErrorResponse(ErrorCode.TYPE_MISMATCH.getCode(), 
                ErrorCode.TYPE_MISMATCH.getMessage(), path);
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "Type mismatch occurred", request);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理HTTP消息不可读异常
     */
    @ExceptionHandler({HttpMessageNotReadableException.class, HttpMessageNotWritableException.class})
    public ResponseEntity<ErrorResponse> handleHttpMessageException(Exception ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        ErrorResponse response = new ErrorResponse(ErrorCode.HTTP_MESSAGE_NOT_READABLE.getCode(), 
                ErrorCode.HTTP_MESSAGE_NOT_READABLE.getMessage(), path);
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "HTTP message not readable", request);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理缺少文件上传参数异常
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(
            MissingServletRequestPartException ex, HttpServletRequest request) {
        
        String path = request.getRequestURI();
        String message = String.format("缺少文件上传参数: %s", ex.getRequestPartName());
        
        ErrorResponse response = new ErrorResponse(ErrorCode.MISSING_SERVLET_REQUEST_PART.getCode(), 
                message, path);
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "Missing servlet request part", request);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * 处理访问拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        ErrorResponse response = new ErrorResponse(ErrorCode.ACCESS_DENIED.getCode(), 
                ErrorCode.ACCESS_DENIED.getMessage(), path);
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "Access denied", request);
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    
    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        String message = String.format("请求的资源 '%s' 不存在", path);
        
        ErrorResponse response = new ErrorResponse(ErrorCode.RESOURCE_NOT_FOUND.getCode(), 
                message, path);
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "No handler found", request);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * 处理异步请求超时异常
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleAsyncRequestTimeoutException(
            AsyncRequestTimeoutException ex, HttpServletRequest request) {
        
        String path = request.getRequestURI();
        ErrorResponse response = new ErrorResponse(ErrorCode.TIMEOUT_ERROR.getCode(), 
                ErrorCode.TIMEOUT_ERROR.getMessage(), path);
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "Async request timeout", request);
        
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(response);
    }
    
    /**
     * 处理其他所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        ErrorResponse response = new ErrorResponse(ErrorCode.SYSTEM_ERROR.getCode(), 
                ErrorCode.SYSTEM_ERROR.getMessage(), path);
        
        if (properties.isIncludeStackTrace()) {
            response.setDetails(getStackTrace(ex));
        }
        
        logException(ex, "Unexpected exception occurred", request);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 构建字段错误信息
     */
    private List<ErrorResponse.FieldError> buildFieldErrors(BindingResult bindingResult) {
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        
        bindingResult.getFieldErrors().forEach(error -> {
            fieldErrors.add(new ErrorResponse.FieldError(
                    error.getField(),
                    error.getRejectedValue(),
                    error.getDefaultMessage()
            ));
        });
        
        bindingResult.getGlobalErrors().forEach(error -> {
            fieldErrors.add(new ErrorResponse.FieldError(
                    error.getObjectName(),
                    null,
                    error.getDefaultMessage()
            ));
        });
        
        return fieldErrors;
    }
    
    /**
     * 记录异常日志
     */
    private void logException(Exception ex, String message, HttpServletRequest request) {
        if (properties.isEnableLogging()) {
            String requestInfo = String.format("[%s] %s", request.getMethod(), request.getRequestURI());
            
            if (ex instanceof BaseException) {
                logger.warn("{} - {}: {}", message, requestInfo, ex.getMessage());
            } else {
                logger.error("{} - {}: {}", message, requestInfo, ex.getMessage(), ex);
            }
        }
    }
    
    /**
     * 获取异常堆栈信息
     */
    private String getStackTrace(Exception ex) {
        if (ex == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getClass().getName()).append(": ").append(ex.getMessage()).append("\n");
        
        StackTraceElement[] stackTrace = ex.getStackTrace();
        int maxLines = Math.min(stackTrace.length, properties.getMaxStackTraceLines());
        
        for (int i = 0; i < maxLines; i++) {
            sb.append("\tat ").append(stackTrace[i]).append("\n");
        }
        
        if (stackTrace.length > maxLines) {
            sb.append("\t... ").append(stackTrace.length - maxLines).append(" more");
        }
        
        return sb.toString();
    }
}
