package ru.itmo.rest.megamarket.spring.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "statistics")
@Data @NoArgsConstructor
public class ShopUnitStatisticUnit {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long stats_id;

    @JsonIgnore
    @Column(name = "itemId")
    private UUID itemId;

    private String name;

    private UUID parentId;

    private ShopUnitType type;

    private Long price;

    private ZonedDateTime date;

    public UUID getId() {
        return itemId;
    }


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

}
