package ru.itmo.yandex.backend.part2.spring.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnit;

import java.util.UUID;

@Repository
public interface ShopUnitRepository extends CrudRepository<ShopUnit, UUID> {
}
