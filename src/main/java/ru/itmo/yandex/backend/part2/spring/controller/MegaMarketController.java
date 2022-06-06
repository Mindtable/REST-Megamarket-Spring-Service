package ru.itmo.yandex.backend.part2.spring.controller;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnit;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnitType;
import ru.itmo.yandex.backend.part2.spring.service.ShopUnitService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController("/api")
public class MegaMarketController {

    private ShopUnitService service;

    @Autowired
    public MegaMarketController(ShopUnitService service) {
        this.service = service;
    }

    @GetMapping("/test")
    public ResponseEntity<?> testController() {
        return new ResponseEntity<>(
                "Hello world",
                HttpStatus.OK
        );
    }

    @PostMapping("/imports")
    public ResponseEntity<?> testImports(@Valid @RequestBody ShopUnitImportRequest unit) {
        for (var elem: unit.getItems()) {
            ShopUnit toDB = new ShopUnit();

            toDB.setDate(unit.getUpdateDate());
            toDB.setId(elem.getId());
            toDB.setName(elem.getName());
            toDB.setPrice(elem.getPrice());
            toDB.setParentId(elem.getParentId());
            toDB.setType(elem.getType());

            service.saveShopUnit(toDB);
        }

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
        System.out.println(id);
        return new ResponseEntity<>(
                service.getByID(id),
                HttpStatus.OK
        );
    }
}
