package ru.itmo.yandex.backend.part2.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.itmo.yandex.backend.part2.spring.exceptions.NoSuchShopUnitException;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnit;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnitType;
import ru.itmo.yandex.backend.part2.spring.service.ShopUnitService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import static javax.transaction.Transactional.TxType.NEVER;

@RestController
//@RequestMapping("/api")
public class MegaMarketController {
    private final ShopUnitService service;
    private final Validator validator;

    private final Logger logger;
    private final PlatformTransactionManager trManager;

    @Autowired
    public MegaMarketController(ShopUnitService service, PlatformTransactionManager transactionManager) {
        this.service = service;
        this.trManager = transactionManager;
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        logger = LoggerFactory.getLogger(MegaMarketController.class);
    }

    @GetMapping("/test")
    public ResponseEntity<?> testController() {
        return new ResponseEntity<>(
                "Hello world",
                HttpStatus.OK
        );
    }
    @Transactional(value = NEVER)
    @PostMapping("/imports")
    public ResponseEntity<?> testImports(@Valid @RequestBody ShopUnitImportRequest unit) throws NoSuchMethodException, MethodArgumentNotValidException {
        TransactionDefinition trDefinition = new DefaultTransactionDefinition();
        TransactionStatus trStatus = trManager.getTransaction(trDefinition);

        for (var elem: unit.getItems()) {
            System.out.println("VALIDATING " + elem.getId());
            var toDB = initShopUnit(elem, unit.getUpdateDate());
            System.out.println(toDB.getRawPrice());
            service.saveShopUnit(toDB);
        }


        trManager.commit(trStatus);
        System.out.println(trStatus.isRollbackOnly());
        System.out.println(trStatus.isCompleted());

        return new ResponseEntity<>(
                unit.getItems().stream().map(ShopUnitImport::getId).collect(Collectors.toList()),
                HttpStatus.OK
        );


    }

    @PostMapping("/testPost")
    public ResponseEntity<?> testPOST(@Valid @RequestBody ShopUnitImport imp) {
        return new ResponseEntity<>(
                "Hello world",
                HttpStatus.OK
        );
    }

    @PostMapping("/testPostImport")
    public ResponseEntity<?> testPostImport(@Valid @RequestBody ShopUnitImportRequest imp) {
        return new ResponseEntity<>(
                "Testing import",
                HttpStatus.OK
        );
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity<?> testGets(@Validated @PathVariable UUID id) {
        var shopUnit = service.getByID(id);
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
//        TransactionDefinition trDefinition = new DefaultTransactionDefinition();
//        TransactionStatus trStatus = trManager.getTransaction(trDefinition);

        service.deleteById(id);
//        trManager.commit(trStatus);
//        System.out.println(trStatus.isRollbackOnly());
//        System.out.println(trStatus.isCompleted());
//        System.out.println("Hello world");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ShopUnit initShopUnit(
            ShopUnitImport elem,
            ZonedDateTime updateTime) throws NoSuchMethodException, MethodArgumentNotValidException {

        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(elem, "ShopUnitImport");
        System.out.println("CHECKING ELEM");
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
        toDB.setParentId(elem.getParentId() == null ? null : service.getByID(elem.getParentId()));

        return toDB;
    }
}
