package ru.itmo.rest.megamarket.spring.service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itmo.rest.megamarket.spring.service.repository.ShopUnitStatisticUnitRepository;
import ru.itmo.rest.megamarket.spring.service.model.ShopUnit;
import ru.itmo.rest.megamarket.spring.service.model.ShopUnitStatisticUnit;

import java.util.Objects;

@Service
public class ShopUnitStatisticUnitService {
    private final ShopUnitStatisticUnitRepository repository;

    private final Logger logger;

    @Autowired
    public ShopUnitStatisticUnitService(ShopUnitStatisticUnitRepository repository) {
        this.repository = repository;
        logger = LoggerFactory.getLogger(ShopUnitStatisticUnitService.class);
    }



    public void saveStatisticUnit(ShopUnit unit) {
        var statisticUnitFromDatabase = repository.findTopByItemIdOrderByDateDesc(unit.getId()).orElse(null);
        if (statisticUnitFromDatabase == null ||
                !Objects.equals(statisticUnitFromDatabase.getRawDate().toInstant(), unit.getRawDate().toInstant())) {
            logger.info("Statistic item found in database: overwriting current state");
            logger.info("id: " + unit.getId() + "; date: " + unit.getDate());
            repository.save(initStatistics(unit));
        } else {
            updateExistingStatistics(statisticUnitFromDatabase, unit);
            repository.save(statisticUnitFromDatabase);
        }
    }

    private ShopUnitStatisticUnit initStatistics(ShopUnit unit) {
        var statisticUnit = new ShopUnitStatisticUnit();

        statisticUnit.setItemId(unit.getId());
        statisticUnit.setType(unit.getType());
        statisticUnit.setDate(unit.getRawDate());
        statisticUnit.setName(unit.getName());
        statisticUnit.setPrice(unit.getPrice());
        statisticUnit.setParentId(unit.getParentId());

        return statisticUnit;
    }

    private void updateExistingStatistics(ShopUnitStatisticUnit statisticUnit, ShopUnit unit) {
        statisticUnit.setName(unit.getName());
        statisticUnit.setPrice(unit.getPrice());
        statisticUnit.setParentId(unit.getParentId());
    }
}
