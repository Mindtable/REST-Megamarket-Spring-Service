package ru.itmo.yandex.backend.part2.spring.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnitStatisticUnit;

@Repository
public interface ShopUnitStatisticUnitRepository extends CrudRepository<ShopUnitStatisticUnit, Long> {
}
