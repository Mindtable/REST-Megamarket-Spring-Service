package ru.itmo.rest.megamarket.spring.service.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.rest.megamarket.spring.service.model.ShopUnitStatisticUnit;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class ShopUnitStatisticResponce {
    private List<ShopUnitStatisticUnit> items;
}
