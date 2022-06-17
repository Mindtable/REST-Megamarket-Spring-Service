package ru.itmo.yandex.backend.part2.spring.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnitStatisticUnit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopUnitStatisticUnitRepository extends CrudRepository<ShopUnitStatisticUnit, Long> {

    Optional<ShopUnitStatisticUnit> findTopByItemIdOrderByDateDesc(UUID uuid);

    Optional<List<ShopUnitStatisticUnit>> findAllByItemId(UUID uuid);
}
