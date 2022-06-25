package ru.itmo.rest.megamarket.spring.service.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.rest.megamarket.spring.service.controller.ShopUnitImport;

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
        logger.info("I'm validating ShopUnit with name:  " + value.getName());
        boolean isValid = false;
        switch (value.getType()) {
            case OFFER -> {
                isValid = value.getPrice() != null && value.getPrice() >= 0;
            } case CATEGORY -> {
                isValid = value.getPrice() == null;
            }
        }
        logger.info(String.valueOf(isValid));
        return isValid;
    }
}
