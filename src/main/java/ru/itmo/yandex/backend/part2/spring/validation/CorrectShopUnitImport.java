package ru.itmo.yandex.backend.part2.spring.validation;

import javax.validation.*;
import java.lang.annotation.*;

@Constraint(validatedBy = ValidatorShopUnitImport.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectShopUnitImport {
    //TODO: change message
    String message() default "THIS IS A TEST MESSAGE";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
