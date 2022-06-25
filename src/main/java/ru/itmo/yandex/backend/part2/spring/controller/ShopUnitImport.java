package ru.itmo.yandex.backend.part2.spring.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnitType;
import ru.itmo.yandex.backend.part2.spring.validation.CorrectParent;
import ru.itmo.yandex.backend.part2.spring.validation.CorrectShopUnitImport;

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
