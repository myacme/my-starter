# Spring Boot 全局异常处理 Starter

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-green.svg)]()
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)]()

一个功能完善的Spring Boot异常处理starter，提供统一的异常处理机制、标准化的错误响应格式和灵活的配置选项。

## 📦 项目简介

该starter旨在简化Spring Boot应用中的异常处理，提供开箱即用的全局异常处理能力，包含：

- **自动化配置**：零配置启动，自动注册全局异常处理器
- **统一响应格式**：标准化的JSON错误响应结构
- **丰富的异常类型**：内置业务异常、校验异常等多种异常类型
- **灵活配置**：支持详细的配置选项控制异常处理行为
- **全面的日志记录**：自动记录异常信息便于问题排查
- **多环境适配**：开发/生产环境差异化配置支持

## 🚀 核心特性

### 🔧 主要功能
- ✅ **自动配置**：基于Spring Boot自动配置机制，引入即用
- ✅ **统一响应**：标准化的错误响应格式，包含错误码、消息、时间戳等
- ✅ **异常分类**：支持业务异常、系统异常、校验异常等多种类型
- ✅ **详细配置**：可配置日志级别、堆栈信息、字段错误详情等
- ✅ **环境适配**：支持不同环境的差异化配置
- ✅ **安全考虑**：生产环境自动隐藏敏感信息

### 🏗️ 架构组成

#### 异常体系
- `BaseException`：基础异常类，所有自定义异常的父类
- `BusinessException`：业务异常，用于处理业务逻辑错误
- `ValidationException`：参数校验异常

#### 核心组件
- `GlobalExceptionHandler`：全局异常处理器，处理各种异常类型
- `ErrorResponse`：统一错误响应模型
- `ErrorCode`：预定义错误码枚举
- `ExceptionHandlerProperties`：配置属性类

## 📦 快速开始

### 1. Maven依赖

在你的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>global-exception-handler-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Gradle依赖

```gradle
implementation 'com.example:global-exception-handler-starter:1.0.0'
```

### 3. 启用自动配置

在Spring Boot主启动类上添加注解（Spring Boot 2.7+通常自动配置）：

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## ⚙️ 配置选项

### 基础配置

```yaml
exception:
  handler:
    # 是否启用全局异常处理器
    enabled: true
    # 是否启用异常日志记录
    enable-logging: true
    # 是否在响应中包含异常堆栈信息
    include-stack-trace: false
    # 异常堆栈信息最大行数
    max-stack-trace-lines: 50
    # 是否启用字段验证错误详情
    include-field-errors: true
```

### 环境差异化配置

**开发环境** (`application-dev.yml`)：
```yaml
exception:
  handler:
    # 开发环境显示堆栈信息便于调试
    include-stack-trace: true
    # 显示更多堆栈信息
    max-stack-trace-lines: 100
```

**生产环境** (`application-prod.yml`)：
```yaml
exception:
  handler:
    # 生产环境隐藏堆栈信息保护安全
    include-stack-trace: false
    # 简化字段错误信息
    include-field-errors: false
```

### 日志级别配置

```yaml
exception:
  handler:
    log-level:
      # 业务异常日志级别
      business: WARN
      # 系统异常日志级别  
      system: ERROR
      # 参数校验异常日志级别
      validation: WARN
```

## 💡 使用示例

### 1. 抛出业务异常

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, 
                "用户不存在", 
                String.format("用户ID: %d", id));
        }
        return userService.findById(id);
    }
    
    @PostMapping
    public User createUser(@Valid @RequestBody CreateUserRequest request) {
        // 参数校验失败会自动抛出 ValidationException
        if (userService.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.RESOURCE_ALREADY_EXISTS, 
                "用户名已存在");
        }
        return userService.create(request);
    }
}
```

### 2. 自定义错误码

```java
public enum CustomErrorCode implements ErrorCode {
    // 用户相关错误
    USER_ALREADY_EXISTS(2003, "用户已存在"),
    USER_ACCOUNT_LOCKED(2004, "用户账户已被锁定"),
    
    // 订单相关错误
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_INVALID(3002, "订单状态不正确");
    
    private final int code;
    private final String message;
    
    CustomErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    @Override
    public int getCode() { return code; }
    
    @Override
    public String getMessage() { return message; }
}
```

### 3. 使用自定义异常

```java
// 基本业务异常
throw new BusinessException(CustomErrorCode.USER_ALREADY_EXISTS);

// 带详细信息的异常
throw new BusinessException(
    CustomErrorCode.ORDER_NOT_FOUND, 
    "订单查询失败",
    String.format("订单号: %s", orderNumber)
);

// 系统异常交给全局处理器处理
throw new RuntimeException("系统内部错误");
```

## 📋 错误响应格式

### 标准响应结构

```json
{
    "code": 2001,
    "message": "资源不存在",
    "details": "用户ID: 123",
    "path": "/api/users/123",
    "timestamp": "2024-01-01 12:00:00",
    "fieldErrors": [
        {
            "field": "username",
            "rejectedValue": "",
            "message": "用户名不能为空"
        }
    ]
}
```

### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | Integer | 错误码 |
| `message` | String | 错误消息 |
| `details` | String | 详细信息（可选） |
| `path` | String | 请求路径 |
| `timestamp` | String | 时间戳 |
| `fieldErrors` | Array | 字段验证错误详情（可选） |

## 🛠️ 项目结构

```
src/main/java/com/example/exception/starter/
├── autoconfigure/
│   └── ExceptionHandlerAutoConfiguration.java  # 自动配置类
├── enums/
│   └── ErrorCode.java                          # 错误码枚举
├── exception/
│   ├── BaseException.java                      # 基础异常
│   ├── BusinessException.java                  # 业务异常
│   └── ValidationException.java                # 校验异常
├── handler/
│   └── GlobalExceptionHandler.java             # 全局异常处理器
├── model/
│   └── ErrorResponse.java                      # 错误响应模型
└── properties/
    └── ExceptionHandlerProperties.java         # 配置属性
```

## 🧪 测试示例

项目包含完整的测试控制器示例：

```java
@RestController
@RequestMapping("/api/example")
public class ExampleController {
    
    @GetMapping("/business-error")
    public String businessError() {
        throw new BusinessException(ErrorCode.BUSINESS_ERROR, "测试业务异常");
    }
    
    @PostMapping("/validation")
    public String testValidation(@Valid @RequestBody UserRequest request) {
        return "验证通过";
    }
    
    @GetMapping("/system-error")
    public String systemError() {
        throw new RuntimeException("测试系统异常");
    }
}
```

## 🎯 最佳实践

### 1. 异常使用建议

```java
// ✅ 正确：业务逻辑异常使用 BusinessException
if (user == null) {
    throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
}

// ✅ 正确：让框架自动处理参数校验
@Valid
public void process(@NotNull @Min(1) Long userId) {
    // ...
}

// ✅ 正确：系统异常让全局处理器处理
try {
    externalService.call();
} catch (Exception e) {
    log.error("外部服务调用失败", e);
    throw e;
}
```

### 2. 错误码命名规范

- 使用数字编码，按模块分类（如：1000-1999系统级，2000-2999业务级）
- 语义明确，避免歧义
- 保持一致性

### 3. 日志记录建议

```java
// 在关键业务节点记录debug日志
log.debug("开始处理用户请求: {}", userId);

// 异常发生时记录适当级别的日志
try {
    businessLogic.process();
} catch (BusinessException e) {
    log.warn("业务处理失败: {}", e.getMessage());
    throw e;
} catch (Exception e) {
    log.error("系统异常: ", e);
    throw e;
}
```

## 🔒 安全考虑

- ❌ **生产环境禁用堆栈信息**：防止泄露系统内部实现细节
- ❌ **敏感信息过滤**：错误响应中不包含数据库连接、密码等敏感信息
- ❌ **错误码设计**：避免通过错误码推测系统架构
- ✅ **详细日志记录**：服务器端记录完整异常信息用于排查

## 📝 版本历史

### v1.0.0 (2024-01-01)
- ✅ 初始版本发布
- ✅ 支持基本异常处理功能
- ✅ 提供统一错误响应格式
- ✅ 实现自动配置机制
- ✅ 内置常见异常类型
- ✅ 支持配置化管理
- ✅ 多环境配置支持

## 🤝 贡献指南

欢迎提交Issue和Pull Request来改进这个项目！

### 开发环境要求
- Java 8+
- Maven 3.6+
- Spring Boot 3.3.4+

### 构建项目
```bash
mvn clean install
```

### 运行测试
```bash
mvn test
```

## 📄 许可证

MIT License

## 🔧 技术支持

如有问题，请提交Issue或联系维护者。

---

<p align="center">Made with ❤️ for Spring Boot developers</p>