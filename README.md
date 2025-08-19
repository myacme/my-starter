# 全局异常处理 Spring Boot Starter

这是一个用于Spring Boot应用程序的全局异常处理starter，提供统一的异常处理机制和标准化的错误响应格式。

## 功能特性

- 🚀 **开箱即用**：引入依赖即可自动启用全局异常处理
- 🎯 **统一响应格式**：标准化的错误响应结构
- 🔧 **灵活配置**：支持多种配置选项，满足不同需求
- 📝 **详细日志**：自动记录异常日志，便于问题排查
- 🛡️ **全面覆盖**：处理常见的Web异常类型
- 🎨 **自定义异常**：提供基础异常类，方便业务扩展

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>global-exception-handler-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 配置属性（可选）

在 `application.yml` 中配置：

```yaml
exception:
  handler:
    enabled: true                    # 是否启用全局异常处理器，默认true
    enable-logging: true             # 是否启用异常日志记录，默认true
    include-stack-trace: false       # 是否在响应中包含异常堆栈信息，默认false
    max-stack-trace-lines: 50        # 异常堆栈信息最大行数，默认50
    include-field-errors: true       # 是否启用字段验证错误详情，默认true
    log-level:
      business: WARN                 # 业务异常日志级别，默认WARN
      system: ERROR                  # 系统异常日志级别，默认ERROR
      validation: WARN               # 参数校验异常日志级别，默认WARN
```

### 3. 使用示例

#### 抛出业务异常

```java
@RestController
public class UserController {
    
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户不存在");
        }
        return user;
    }
}
```

#### 参数校验

```java
@RestController
public class UserController {
    
    @PostMapping("/user")
    public User createUser(@Valid @RequestBody UserRequest request) {
        // 如果request中的字段不满足校验规则，会自动抛出MethodArgumentNotValidException
        // 全局异常处理器会捕获并返回标准化的错误响应
        return userService.create(request);
    }
}

public class UserRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @Min(value = 1, message = "年龄必须大于0")
    private Integer age;
    
    // getters and setters...
}
```

## 错误响应格式

所有异常都会返回统一的JSON格式响应：

```json
{
  "code": 2001,
  "message": "用户不存在",
  "path": "/api/user/123",
  "timestamp": "2024-01-01 12:00:00",
  "details": "详细错误信息（仅在开启堆栈跟踪时显示）",
  "fieldErrors": [
    {
      "field": "username",
      "rejectedValue": "",
      "message": "用户名不能为空"
    }
  ]
}
```

## 支持的异常类型

### 业务异常
- `BaseException` - 基础异常类
- `BusinessException` - 业务异常
- `ValidationException` - 数据校验异常

### Spring Web异常
- `MethodArgumentNotValidException` - 参数校验失败
- `BindException` - 参数绑定异常
- `ConstraintViolationException` - 约束违反异常
- `HttpRequestMethodNotSupportedException` - 请求方法不支持
- `HttpMediaTypeNotSupportedException` - 媒体类型不支持
- `MissingServletRequestParameterException` - 缺少请求参数
- `TypeMismatchException` - 参数类型不匹配
- `HttpMessageNotReadableException` - HTTP消息不可读
- `MissingServletRequestPartException` - 缺少文件上传参数
- `NoHandlerFoundException` - 404异常
- `AsyncRequestTimeoutException` - 异步请求超时
- `AccessDeniedException` - 访问拒绝

### 系统异常
- `Exception` - 所有未捕获的异常

## 错误码定义

| 错误码范围 | 类型 | 说明 |
|---------|------|------|
| 0 | 成功 | 操作成功 |
| 1000-1999 | 系统级别错误 | 参数错误、类型不匹配等 |
| 2000-2999 | 业务级别错误 | 资源不存在、业务逻辑错误等 |
| 3000-3999 | 权限相关错误 | 未授权、访问拒绝等 |
| 4000-4999 | 外部服务错误 | 外部服务调用失败等 |

## 自定义异常

继承 `BaseException` 创建自定义异常：

```java
public class CustomException extends BaseException {
    public CustomException(String message) {
        super(ErrorCode.BUSINESS_ERROR, message);
    }
    
    public CustomException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
```

## 扩展错误码

在 `ErrorCode` 枚举中添加新的错误码：

```java
public enum ErrorCode {
    // 现有错误码...
    
    // 自定义业务错误码
    USER_NOT_FOUND(2100, "用户不存在"),
    ORDER_NOT_FOUND(2101, "订单不存在");
    
    // 构造函数和方法...
}
```

## 注意事项

1. **生产环境安全**：生产环境建议设置 `include-stack-trace: false`，避免泄露敏感信息
2. **日志级别**：根据实际需要调整不同类型异常的日志级别
3. **异常处理顺序**：更具体的异常处理器会优先于通用的异常处理器
4. **性能考虑**：异常处理会有一定的性能开销，不建议将异常作为正常的业务流程控制

## 版本要求

- Java 8+
- Spring Boot 2.0+

## 许可证

MIT License
