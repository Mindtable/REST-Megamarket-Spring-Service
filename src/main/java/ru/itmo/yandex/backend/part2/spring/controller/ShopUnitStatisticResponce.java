package ru.itmo.yandex.backend.part2.spring.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnitStatisticUnit;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class ShopUnitStatisticResponce {
    private List<ShopUnitStatisticUnit> items;
}
