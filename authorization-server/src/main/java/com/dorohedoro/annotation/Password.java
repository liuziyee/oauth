package com.dorohedoro.annotation;

import com.dorohedoro.validation.PassayValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PassayValidator.class)
public @interface Password {
    
    String message() default "无效的密码";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
