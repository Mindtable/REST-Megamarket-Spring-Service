package ru.itmo.rest.megamarket.spring.service.validation;

import javax.validation.*;
import java.lang.annotation.*;

@Constraint(validatedBy = ValidatorShopUnitImport.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectShopUnitImport {
    String message() default "ShopUnitImport validation failed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
