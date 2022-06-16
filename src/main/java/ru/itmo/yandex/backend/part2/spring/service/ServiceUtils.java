package ru.itmo.yandex.backend.part2.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServiceUtils {
    private static ServiceUtils instance;

    @Autowired
    private ShopUnitService shopUnitService;

    /* Post constructor */

    @PostConstruct
    public void fillInstance() {
        instance = this;
    }

    /*static methods */

    public static ShopUnitService getShopUnitService() {
        return instance.shopUnitService;
    }
}