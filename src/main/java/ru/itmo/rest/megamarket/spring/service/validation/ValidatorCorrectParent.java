package ru.itmo.rest.megamarket.spring.service.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.itmo.rest.megamarket.spring.service.controller.ShopUnitImport;
import ru.itmo.rest.megamarket.spring.service.model.ShopUnitType;
import ru.itmo.rest.megamarket.spring.service.service.ServiceUtils;
import ru.itmo.rest.megamarket.spring.service.service.ShopUnitService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class ValidatorCorrectParent implements ConstraintValidator<CorrectParent, ShopUnitImport> {
    @Autowired
    private ShopUnitService service;

    private Logger logger;

    @Override
    public void initialize(CorrectParent constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        logger = LoggerFactory.getLogger(ValidatorCorrectParent.class);
        service = ServiceUtils.getShopUnitService();
    }

    @Override
    public boolean isValid(ShopUnitImport value, ConstraintValidatorContext context) {
        logger.info("Validating if parent exists and correct...");
        var parent = value.getParentId() == null ? null : service.getByID(value.getParentId());
        logger.info("isValid: " + (parent == null || parent.getType() != ShopUnitType.OFFER));
        return value.getParentId() == null || (parent != null && parent.getType() != ShopUnitType.OFFER);
    }

}
