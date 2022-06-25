package ru.itmo.rest.megamarket.spring.service.exceptions;

public class UndefinedThingException  extends  RuntimeException {
    public UndefinedThingException(String message) {
        super(message);
    }
}
