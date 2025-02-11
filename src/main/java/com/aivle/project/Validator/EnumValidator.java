package com.aivle.project.Validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValidatorConstraint.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValidator {
    Class<? extends Enum<?>> enumClass();  // 사용할 Enum 클래스 지정
    String message() default "올바른 값을 입력하세요.";  // 기본 메시지
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
