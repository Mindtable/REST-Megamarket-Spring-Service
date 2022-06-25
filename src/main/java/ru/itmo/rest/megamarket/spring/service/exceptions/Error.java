package ru.itmo.rest.megamarket.spring.service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor @AllArgsConstructor
public class Error {

    @NotNull
    private Integer code;

    @NotBlank
    private String message;
}
