package ru.itmo.rest.megamarket.spring.service.exceptions;

public class ShopUnitTypeChangeException extends RuntimeException {
    public ShopUnitTypeChangeException(String message) {
        super(message);
    }
}
