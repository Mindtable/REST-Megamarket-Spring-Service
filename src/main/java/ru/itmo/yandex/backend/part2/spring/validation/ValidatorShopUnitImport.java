package ru.itmo.yandex.backend.part2.spring.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.yandex.backend.part2.spring.controller.ShopUnitImport;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidatorShopUnitImport implements ConstraintValidator<CorrectShopUnitImport, ShopUnitImport> {

    private Logger logger;

    @Override
    public void initialize(CorrectShopUnitImport constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        logger = LoggerFactory.getLogger(ValidatorShopUnitImport.class);
    }

    @Override
    public boolean isValid(ShopUnitImport value, ConstraintValidatorContext context) {
        logger.info("I AM VALIDATING " + value.getName() + "!!!!");
        boolean isValid = false;
        switch (value.getType()) {
            case OFFER -> {
                isValid = value.getPrice() != null;
            } case CATEGORY -> {
                isValid = value.getPrice() == null;
            }
        }
        return isValid;
    }
}