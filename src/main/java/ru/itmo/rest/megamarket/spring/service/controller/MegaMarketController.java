package ru.itmo.rest.megamarket.spring.service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.itmo.rest.megamarket.spring.service.model.ShopUnit;
import ru.itmo.rest.megamarket.spring.service.model.ShopUnitType;
import ru.itmo.rest.megamarket.spring.service.service.ShopUnitService;
import ru.itmo.rest.megamarket.spring.service.service.ShopUnitStatisticUnitService;
import ru.itmo.rest.megamarket.spring.service.exceptions.NoSuchShopUnitException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Pattern;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static javax.transaction.Transactional.TxType.NEVER;

@RestController
public class MegaMarketController {
    private final ShopUnitService shopUnitService;
    private final ShopUnitStatisticUnitService statisticUnitService;
    private final Validator validator;

    private final Logger logger;
    private final PlatformTransactionManager trManager;

    @Autowired
    public MegaMarketController(ShopUnitService service, ShopUnitStatisticUnitService statisticUnitService, PlatformTransactionManager transactionManager) {
        this.shopUnitService = service;
        this.statisticUnitService = statisticUnitService;
        this.trManager = transactionManager;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        logger = LoggerFactory.getLogger(MegaMarketController.class);
    }

    @Transactional(value = NEVER)
    @PostMapping("/imports")
    public ResponseEntity<?> imports(@Valid @RequestBody ShopUnitImportRequest unit) throws NoSuchMethodException, MethodArgumentNotValidException {
        TransactionDefinition trDefinition = new DefaultTransactionDefinition();
        TransactionStatus trStatus = trManager.getTransaction(trDefinition);

        for (var shopUnit: unit.getItems()) {
            var toDB = initShopUnit(shopUnit, ZonedDateTime.parse(unit.getUpdateDate()));
            var parentsNeedToBeUpdated = shopUnitService.saveShopUnit(toDB);
            statisticUnitService.saveStatisticUnit(toDB);

            if (toDB.getType() == ShopUnitType.CATEGORY &&
                    shopUnitService.getByID(toDB.getId()).getChildCount() == 0) {
                // You don't need to update parent tree, if you're updating empty category
                continue;
            }

            parentsNeedToBeUpdated.forEach(this::updateStatistic);
        }

        trManager.commit(trStatus);

        return new ResponseEntity<>(
                HttpStatus.OK
        );


    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<?> getNode(@Validated @PathVariable UUID id) {
        var shopUnit = shopUnitService.getByID(id);
        if (shopUnit == null) {
            throw new NoSuchShopUnitException();
        }
        return new ResponseEntity<>(
                shopUnit,
                HttpStatus.OK
        );
    }

    @Transactional(value = NEVER)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> testDelete(@Validated @PathVariable UUID id) {
        shopUnitService.deleteById(id);
        return new ResponseEntity<>(
                HttpStatus.OK);
    }

    @GetMapping("/sales")
    public ResponseEntity<?> getUpdatedSales(@Valid @Pattern(regexp = "^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$")
                                                 @RequestParam String date) {
        return new ResponseEntity<>(
                new ShopUnitStatisticResponce(shopUnitService.getAllUpdatedOfferWithin24Hours(ZonedDateTime.parse(date))),
                HttpStatus.OK
        );
    }

    @GetMapping("/node/{id}/statistic")
    public ResponseEntity<?> getStatisticForShopUnit(
            @Valid @PathVariable UUID id,
            @Valid @Pattern(regexp = "^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$")
            @RequestParam Optional<String> dateStart,
            @Valid @Pattern(regexp = "^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$")
            @RequestParam Optional<String> dateEnd) {

        return new ResponseEntity<>(
                new ShopUnitStatisticResponce(shopUnitService.getStatisticFomShopUnit(id,
                        dateStart.isEmpty() ? null : ZonedDateTime.parse(dateStart.get()),
                        dateEnd.isEmpty() ? null : ZonedDateTime.parse(dateEnd.get()))),
                HttpStatus.OK
        );
    }

    private ShopUnit initShopUnit(
            ShopUnitImport elem,
            ZonedDateTime updateTime) throws NoSuchMethodException, MethodArgumentNotValidException {

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(elem, "ShopUnitImport");
        System.out.println("CHECKING ELEMENT");
        if (!validator.validate(elem).isEmpty()) {
            throw new MethodArgumentNotValidException(
                    new MethodParameter(this.getClass().getDeclaredMethod("initShopUnit", ShopUnitImport.class, ZonedDateTime.class), 0),
                    errors
            );
        }
        ShopUnit toDB = new ShopUnit();

        toDB.setType(elem.getType());
        toDB.setChildCount(toDB.getType() == ShopUnitType.CATEGORY ? 0L : 1L);
        toDB.setPrice(toDB.getType() == ShopUnitType.CATEGORY ? 0L : elem.getPrice());

        toDB.setDate(updateTime);
        toDB.setId(elem.getId());
        toDB.setName(elem.getName());
        toDB.setParentId(elem.getParentId() == null ? null : shopUnitService.getByID(elem.getParentId()));

        return toDB;
    }

    private void updateStatistic(UUID shopUnitId) {
        while (shopUnitId != null) {
            var shopUnit = shopUnitService.getByID(shopUnitId);
            statisticUnitService.saveStatisticUnit(shopUnit);
            shopUnitId = shopUnit.getParentId();
        }
    }
}
