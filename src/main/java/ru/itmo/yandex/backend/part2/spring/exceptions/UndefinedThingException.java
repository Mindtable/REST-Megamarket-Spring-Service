package ru.itmo.yandex.backend.part2.spring.exceptions;

public class UndefinedThingException  extends  RuntimeException {
    public UndefinedThingException(String message) {
        super(message);
    }
}
