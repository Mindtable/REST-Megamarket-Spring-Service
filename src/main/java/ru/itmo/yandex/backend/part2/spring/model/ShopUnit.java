package ru.itmo.yandex.backend.part2.spring.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micrometer.core.lang.Nullable;
import lombok.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Entity
@Table(name = "shop_units")
@Data @NoArgsConstructor @AllArgsConstructor
public class ShopUnit {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @Column(name = "parentId", nullable = true)
    private UUID parentId;

    @Column(name = "type", nullable = false)
    private ShopUnitType type;

    @Column(name = "price", nullable = true)
    private Long price;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(cascade = {CascadeType.PERSIST})
    @JoinColumn(name="parentId")
    private Set<ShopUnit> children;

    public String getDate() {
        System.out.println(date);
        return date.format(DateTimeFormatter.ISO_INSTANT).toString();
    }
}
