package com.example.exception.starter.example;

import com.example.exception.starter.enums.ErrorCode;
import com.example.exception.starter.exception.BusinessException;
import com.example.exception.starter.exception.ValidationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 异常处理示例控制器
 */
@RestController
@RequestMapping("/api/example")
public class ExampleController {
    
    /**
     * 测试业务异常
     */
    @GetMapping("/business-error")
    public String businessError() {
        throw new BusinessException(ErrorCode.BUSINESS_ERROR, "这是一个业务异常示例");
    }
    
    /**
     * 测试自定义业务异常
     */
    @GetMapping("/custom-business-error")
    public String customBusinessError() {
        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户不存在");
    }
    
    /**
     * 测试校验异常
     */
    @GetMapping("/validation-error")
    public String validationError() {
        throw new ValidationException("数据校验失败");
    }
    
    /**
     * 测试系统异常
     */
    @GetMapping("/system-error")
    public String systemError() {
        throw new RuntimeException("这是一个系统异常示例");
    }
    
    /**
     * 测试参数校验异常 - @Valid
     */
    @PostMapping("/validation")
    public String testValidation(@Valid @RequestBody UserRequest request) {
        return "验证通过";
    }
    
    /**
     * 测试缺少请求参数异常
     */
    @GetMapping("/missing-param")
    public String testMissingParam(@RequestParam String name) {
        return "Hello " + name;
    }
    
    /**
     * 测试类型不匹配异常
     */
    @GetMapping("/type-mismatch")
    public String testTypeMismatch(@RequestParam Integer age) {
        return "Age is " + age;
    }
    
    /**
     * 用户请求对象
     */
    public static class UserRequest {
        
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        @NotNull(message = "年龄不能为空")
        private Integer age;
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public Integer getAge() {
            return age;
        }
        
        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
