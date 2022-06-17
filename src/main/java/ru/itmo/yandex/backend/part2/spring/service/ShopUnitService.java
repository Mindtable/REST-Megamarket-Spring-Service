package ru.itmo.yandex.backend.part2.spring.service;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import java.util.stream.Collectors;

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
            logger.info("ShopUnit found in database: updating");
            logger.info("id: " + unit.getId() + "; previous date: " + DBUnit.get().getDate());

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

    public List<ShopUnit> getAllUpdatedOfferWithin24Hours(ZonedDateTime date) {
        return repository.findAllByDateBetween(date.minusHours(24), date)
                .stream()
                .filter((ShopUnit unit) -> unit.getType() == ShopUnitType.OFFER)
                .collect(Collectors.toList());
    }

    public List<ShopUnitStatisticUnit> getStatisticFomShopUnit(UUID id, ZonedDateTime dateStart, ZonedDateTime dateEnd) {
        var shopUnit = repository.findById(id);

        if (shopUnit.isEmpty()) {
            throw new NoSuchShopUnitException();
        }

        return shopUnit.get().getStats()
                .stream()
                .filter((ShopUnitStatisticUnit unit) ->
                { return dateStart == null ||
                         unit.getRawDate().isAfter(dateStart) ||
                         unit.getRawDate().isEqual(dateStart);
                })
                .filter((ShopUnitStatisticUnit unit) ->
                { return dateEnd == null ||
                        unit.getRawDate().isBefore(dateEnd);
                })
                .collect(Collectors.toList());
    }

    private UUID updateShopUnit(ShopUnit unit, ShopUnit DBUnit) {
        UUID updatedParent = null;

        if (DBUnit.getType() != unit.getType()) {
            throw new UndefinedThingException("You was trying to change type of ShopUnit from category to offer or backwards");
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

    private void updatePriceAndChildCount(ShopUnit unit) {
        var newData = calculateNewPriceForCategory(unit);
        unit.setChildCount(newData.getChildCount());
        unit.setPrice(newData.getPrice());
    }

    private NewPriceForCategory calculateNewPriceForCategory(ShopUnit unit) {
        logger.info("Calculating new price for parent ShopUnit with id: " + unit.getId());
        var result = new NewPriceForCategory();

        for (var child: unit.getChildren()) {
            result.increaseChildCount(child.getChildCount());
            result.increasePrice(child.getRawPrice());
        }

        return result;
    }

    private void updateShopUnitParent(UUID unitId, ZonedDateTime date) {
        while (unitId != null) {
            var currentShopUnit = getByID(unitId);

            if (currentShopUnit.getType() == ShopUnitType.OFFER) {
                throw new UndefinedThingException("Was provided parent of offer type to updateShopUnitParent");
            }

            updatePriceAndChildCount(currentShopUnit);

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
class NewPriceForCategory {
    private long price;
    private long childCount;

    public NewPriceForCategory() {
        price = 0;
        childCount = 0;
    }

    public void increasePrice(Long value) {
        price += value == null ? 0 : value;
    }

    public void increaseChildCount(Long value) {
        childCount += value == null ? 0 : value;
    }
}