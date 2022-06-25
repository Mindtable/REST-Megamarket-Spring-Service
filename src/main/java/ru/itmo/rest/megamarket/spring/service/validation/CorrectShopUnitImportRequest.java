package ru.itmo.rest.megamarket.spring.service.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidatorShopUnitImportRequest.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectShopUnitImportRequest {
    String message() default "ShopUnitImport request validation failed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
