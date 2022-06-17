package ru.itmo.yandex.backend.part2.spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "statistics")
@Data @NoArgsConstructor
public class ShopUnitStatisticUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long stats_id;

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

//    public String getDate() {
//        return date.format(DateTimeFormatter.ISO_INSTANT).toString();
//    }
//
//    @JsonIgnore
//    public ZonedDateTime getRawDate() {
//        return date;
//    }

}
