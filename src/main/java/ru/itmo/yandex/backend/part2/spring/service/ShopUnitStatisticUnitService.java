package ru.itmo.yandex.backend.part2.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnitStatisticUnit;
import ru.itmo.yandex.backend.part2.spring.repository.ShopUnitStatisticUnitRepository;

@Service
public class ShopUnitStatisticUnitService {


    private final ShopUnitStatisticUnitRepository repository;

    @Autowired
    public ShopUnitStatisticUnitService(ShopUnitStatisticUnitRepository repository) {
        this.repository = repository;
    }

    public void save(ShopUnitStatisticUnit unit) {
        repository.save(unit);
    }
}
