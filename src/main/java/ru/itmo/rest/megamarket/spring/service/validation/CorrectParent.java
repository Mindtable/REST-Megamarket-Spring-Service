package ru.itmo.rest.megamarket.spring.service.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidatorCorrectParent.class)
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectParent {
    String message() default "ShopUnit has incorrect parent";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
