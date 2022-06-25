package ru.itmo.rest.megamarket.spring.service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.rest.megamarket.spring.service.model.ShopUnitStatisticUnit;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopUnitStatisticUnitRepository extends CrudRepository<ShopUnitStatisticUnit, Long> {

    Optional<ShopUnitStatisticUnit> findTopByItemIdOrderByDateDesc(UUID uuid);

    Optional<List<ShopUnitStatisticUnit>> findAllByItemId(UUID uuid);

    List<ShopUnitStatisticUnit> findAllByDateBetween(ZonedDateTime start, ZonedDateTime end);
}
