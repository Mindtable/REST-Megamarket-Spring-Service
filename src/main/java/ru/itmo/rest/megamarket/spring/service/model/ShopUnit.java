package ru.itmo.rest.megamarket.spring.service.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.itmo.rest.megamarket.spring.service.exceptions.UndefinedThingException;

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

    @ManyToOne
    @JoinColumn(name = "fk_parentid")
    private ShopUnit parentId;

    @Column(name = "type", nullable = false)
    private ShopUnitType type;

    @Column(name = "price", nullable = true)
    private Long price;

    @JsonIgnore
    private Long childCount;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentId")
    private Set<ShopUnit> children = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "itemId")
    private Set<ShopUnitStatisticUnit> stats;

    public String getDate() {
        return DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneId.from(ZoneOffset.UTC))
                .format(date.toInstant());
    }

    @JsonIgnore
    public ZonedDateTime getRawDate() {
        return date;
    }

    public Long getPrice() {
        return childCount == 0 ? null : price == null ? 0 : price / childCount;
    }

    @JsonIgnore
    public Long getRawPrice() {
        return price;
    }

    public UUID getParentId() {
        return parentId == null ? null : parentId.getId();
    }

    public Set<ShopUnit> getChildren() {
        switch (type) {
            case CATEGORY -> {
                return children;
            } case OFFER -> {
                return null;
            }
        }
        throw new UndefinedThingException("Unsupported ShopUnit type");
    }



}
