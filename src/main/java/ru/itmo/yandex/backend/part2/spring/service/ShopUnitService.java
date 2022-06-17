package ru.itmo.yandex.backend.part2.spring.service;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.itmo.yandex.backend.part2.spring.exceptions.NoSuchShopUnitException;
import ru.itmo.yandex.backend.part2.spring.exceptions.UndefinedThingException;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnit;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnitStatisticUnit;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnitType;
import ru.itmo.yandex.backend.part2.spring.repository.ShopUnitRepository;
import ru.itmo.yandex.backend.part2.spring.repository.ShopUnitStatisticUnitRepository;
import ru.itmo.yandex.backend.part2.spring.validation.ValidatorCorrectParent;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ShopUnitService {

    private ShopUnitRepository repository;
    private ShopUnitStatisticUnitRepository stats_repo;
    private final Logger logger;

    @Autowired
    public ShopUnitService(ShopUnitRepository repository, ShopUnitStatisticUnitRepository stats_repo) {
        this.repository = repository;
        this.stats_repo = stats_repo;
        logger = LoggerFactory.getLogger(ValidatorCorrectParent.class);
    }

    public List<UUID> saveShopUnit(ShopUnit unit) {
        var DBUnit = repository.findById(unit.getId());
        var parentsNeedToBeUpdated = new ArrayList<UUID>();

        if (unit.getParentId() != null) {
            parentsNeedToBeUpdated.add(unit.getParentId());
        }

        if (DBUnit.isPresent()) {
            logger.info("item has been found");

            var existingDatabaseUnit = DBUnit.get();

            var oldParentId = updateShopUnit(unit, existingDatabaseUnit);

            if (oldParentId != null) {
                removeChildByIdAndPersist(oldParentId, existingDatabaseUnit.getId());
                parentsNeedToBeUpdated.add(oldParentId);
            }

            addChildAndPersist(existingDatabaseUnit.getParentId(), existingDatabaseUnit);
        } else if (unit.getParentId() != null){
            addChildAndPersist(unit.getParentId(), unit);
        } else {
            repository.save(unit);
        }
        parentsNeedToBeUpdated.forEach((UUID parentId) -> {
            updateShopUnitParent(parentId, unit.getRawDate());
        });
        return parentsNeedToBeUpdated;
    }

    public void deleteById(UUID id) {
        if (getByID(id) == null) {
            throw new NoSuchShopUnitException();
        }
        var itemToDelete = getByID(id);
        var parentId = itemToDelete == null ? null : itemToDelete.getParentId();
        repository.deleteById(id);
        assert itemToDelete != null;
        removeChildByIdAndPersist(itemToDelete.getParentId(), itemToDelete.getId());
        updateShopUnitParent(parentId, null);
    }

    private UUID updateShopUnit(ShopUnit unit, ShopUnit DBUnit) {
        UUID updatedParent = null;

        if (DBUnit.getType() != unit.getType()) {
            //TODO: хуйня какая-то
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY);
        }
        if (DBUnit.getType() == ShopUnitType.OFFER) {
            DBUnit.setPrice(unit.getRawPrice());
        }

        if (!Objects.equals(DBUnit.getParentId(), unit.getParentId())) {
            // If parent has changed, I should update old parent tree too

            updatedParent = DBUnit.getParentId();
            DBUnit.setParentId(unit.getParentId() == null ? null : getByID(unit.getParentId()));
        }

        DBUnit.setName(unit.getName());
        DBUnit.setDate(unit.getRawDate());

        return updatedParent;
    }

    private void recalculatePriceAndChildCount(ShopUnit unit) {
        var newData = calculateChildCount(unit);
        unit.setChildCount(newData.getChildCount());
        unit.setPrice(newData.getPrice());
    }

    private AveragePriceResult calculateChildCount(ShopUnit unit) {
        logger.info("Updating parent " + unit.getId());
        var result = new AveragePriceResult();

        for (var child: unit.getChildren()) {
            result.incChildCount(child.getChildCount());
            result.incPrice(child.getRawPrice());
        }

        return result;
    }

    private void updateShopUnitParent(UUID unitId, ZonedDateTime date) {
        while (unitId != null) {
            var currentShopUnit = getByID(unitId);

            if (currentShopUnit.getType() == ShopUnitType.OFFER) {
                throw new UndefinedThingException();
            }

            recalculatePriceAndChildCount(currentShopUnit);

            if (date != null) {
                currentShopUnit.setDate(date);
            }

            repository.save(currentShopUnit);

            unitId = currentShopUnit.getParentId();
        }
    }

    public ShopUnit getByID(UUID id) {
        if (id == null) return null;
        return repository.findById(id).orElse(null);
    }

    private void removeChildByIdAndPersist(UUID databaseEntity, UUID childId) {
        var oldParent = getByID(databaseEntity);

        if (oldParent == null) {
            return;
        }

        oldParent.getChildren().removeIf((ShopUnit u) -> {
            return Objects.equals(u.getId(), childId);
        });

        repository.save(oldParent);
    }

    private void addChildAndPersist(UUID databaseEntity, ShopUnit newChild) {
        var newParent = getByID(databaseEntity);
        if (newParent != null) {
            newParent.getChildren().add(newChild);
            repository.save(newParent);
        }
    }


}

//TODO: rename class
@Data
class AveragePriceResult {
    private long price;
    private long childCount;

    public AveragePriceResult() {
        price = 0;
        childCount = 0;
    }

    public void incPrice(Long value) {
        price += value == null ? 0 : value;
    }

    public void incChildCount(Long value) {
        childCount += value == null ? 0 : value;
    }
}