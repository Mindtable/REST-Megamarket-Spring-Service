package ru.itmo.rest.megamarket.spring.service.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.rest.megamarket.spring.service.controller.ShopUnitImport;
import ru.itmo.rest.megamarket.spring.service.controller.ShopUnitImportRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidatorShopUnitImportRequest
        implements ConstraintValidator<CorrectShopUnitImportRequest, ShopUnitImportRequest> {

    private Logger logger;

    @Override
    public void initialize(CorrectShopUnitImportRequest constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        logger = LoggerFactory.getLogger(ValidatorShopUnitImportRequest.class);
    }

    @Override
    public boolean isValid(ShopUnitImportRequest value, ConstraintValidatorContext context) {
        logger.info("Validating ShopUnitImportRequest");
        return value.getItems().stream().map(ShopUnitImport::getId).distinct().count() == value.getItems().size();
    }
}
