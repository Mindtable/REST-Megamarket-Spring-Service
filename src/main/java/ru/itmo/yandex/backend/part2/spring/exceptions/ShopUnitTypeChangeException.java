package ru.itmo.yandex.backend.part2.spring.exceptions;

public class ShopUnitTypeChangeException extends RuntimeException {
    public ShopUnitTypeChangeException(String message) {
        super(message);
    }
}
