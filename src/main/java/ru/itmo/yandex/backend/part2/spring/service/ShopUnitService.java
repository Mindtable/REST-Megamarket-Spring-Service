package ru.itmo.yandex.backend.part2.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnit;
import ru.itmo.yandex.backend.part2.spring.repository.ShopUnitRepository;

import java.util.UUID;

@Service
public class ShopUnitService {

    private ShopUnitRepository repository;

    @Autowired
    public ShopUnitService(ShopUnitRepository repository) {
        this.repository = repository;
    }

    public UUID saveShopUnit(ShopUnit unit) {
        var DBUnit = repository.findById(unit.getId());

        if (DBUnit.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY);
        }

        repository.save(unit);
        return unit.getId();
    }

    public ShopUnit getByID(UUID id) {
        return repository.findById(id).orElse(null);
    }
}
