package ru.itmo.yandex.backend.part2.spring.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import ru.itmo.yandex.backend.part2.spring.controller.ShopUnitImport;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnitType;
import ru.itmo.yandex.backend.part2.spring.service.ServiceUtils;
import ru.itmo.yandex.backend.part2.spring.service.ShopUnitService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

@Component
public class ValidatorCorrectParent implements ConstraintValidator<CorrectParent, ShopUnitImport> {

    @Autowired
    private ShopUnitService service;

//    @Autowired
//    private AutowireCapableBeanFactory beanFactory;

    private Logger logger;

    @Override
    public void initialize(CorrectParent constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        logger = LoggerFactory.getLogger(ValidatorCorrectParent.class);
        service = ServiceUtils.getShopUnitService();
//        service = beanFactory.createBean(ShopUnitService.class);
    }

    @Override
    public boolean isValid(ShopUnitImport value, ConstraintValidatorContext context) {
        logger.info("Validating if parent exists and correct...");
        var parent = value.getParentId() == null ? null : service.getByID(value.getParentId());
        logger.info("isValid: " +
                String.valueOf(parent == null || parent.getType() != ShopUnitType.OFFER));
        return value.getParentId() == null || (parent != null && parent.getType() != ShopUnitType.OFFER);
    }

}
