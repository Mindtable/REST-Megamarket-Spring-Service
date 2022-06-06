package ru.itmo.yandex.backend.part2.spring.controller;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.yandex.backend.part2.spring.validation.CorrectShopUnitImportRequest;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@CorrectShopUnitImportRequest
public class ShopUnitImportRequest {
    @NotNull
    private List<ShopUnitImport> items;

    @NotNull
    private ZonedDateTime updateDate;
}