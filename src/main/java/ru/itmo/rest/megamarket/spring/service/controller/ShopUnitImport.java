package ru.itmo.rest.megamarket.spring.service.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.rest.megamarket.spring.service.model.ShopUnitType;
import ru.itmo.rest.megamarket.spring.service.validation.CorrectParent;
import ru.itmo.rest.megamarket.spring.service.validation.CorrectShopUnitImport;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@CorrectShopUnitImport
@CorrectParent
public class ShopUnitImport {

    @NotNull
    private UUID id;

    @NotNull
    private String name;

    private UUID parentId;

    @NotNull
    private ShopUnitType type;

    private Long price;
}
