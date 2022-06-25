package ru.itmo.yandex.backend.part2.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.yandex.backend.part2.spring.configuration.Config;
import ru.itmo.yandex.backend.part2.spring.controller.MegaMarketController;
import ru.itmo.yandex.backend.part2.spring.controller.ShopUnitImport;
import ru.itmo.yandex.backend.part2.spring.controller.ShopUnitImportRequest;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnitStatisticUnit;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnitType;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.json;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(Config.class)
public class GetMethodTests {

    public static final
    MediaType APPLICATION_JSON_UTF8 = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    @Autowired
    private MegaMarketController controller;

    @Autowired
    @Qualifier("validationFailedJson")
    private String validationFailedJson;

    @Autowired
    @Qualifier("itemNotFoundJson")
    private String itemNotFoundJson;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectWriter ow;

    public GetMethodTests() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        this.ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    public void incorrectUUIDPassed() throws Exception {
        this.mockMvc.perform(
                        get("/nodes/123e4567-q89b-12d3-a456-426655440000"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(validationFailedJson));

        this.mockMvc.perform(
                        get("/node/123e4567-q89b-12d3-a456-426655440000/statistic"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(validationFailedJson));
    }

    @Test
    public void averagePriceChangeCorrectly() throws Exception {

        var rootId = UUID.randomUUID();

        var firstImportItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                rootId,
                                "root category",
                                null,
                                ShopUnitType.CATEGORY,
                                null
                        ),
                        new ShopUnitImport(
                                UUID.randomUUID(),
                                "child offer 1",
                                rootId,
                                ShopUnitType.OFFER,
                                100L
                        ),
                        new ShopUnitImport(
                                UUID.randomUUID(),
                                "child offer 2",
                                rootId,
                                ShopUnitType.OFFER,
                                200L
                        ),
                        new ShopUnitImport(
                                UUID.randomUUID(),
                                "child offer 3",
                                rootId,
                                ShopUnitType.OFFER,
                                300L
                        )
                ),
                "2024-01-04T21:00:00.000Z"
        );

        this.mockMvc.perform(
                post("/imports")
                .contentType(APPLICATION_JSON_UTF8)
                .content(ow.writeValueAsString(firstImportItem)))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                get("/nodes/" + rootId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").isNumber())
                .andExpect(jsonPath("$.price", is(200)));

        var secondImportItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                UUID.randomUUID(),
                                "child offer 4",
                                rootId,
                                ShopUnitType.OFFER,
                                1000000L
                        )
                ),
                "2024-02-04T21:00:00.000Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(secondImportItem)))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                        get("/nodes/" + rootId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").isNumber())
                .andExpect(jsonPath("$.price", is(250150)))
                .andExpect(jsonPath("$.date", is("2024-02-04T21:00:00.000Z")));

    }

    @Test
    public void getLastDayStatisticsCorrectly() throws Exception {

        var rootId = UUID.randomUUID();
        var offerId = UUID.randomUUID();

        var firstImportItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                rootId,
                                "root category",
                                null,
                                ShopUnitType.CATEGORY,
                                null
                        ),
                        new ShopUnitImport(
                                offerId,
                                "child offer 1",
                                rootId,
                                ShopUnitType.OFFER,
                                100L
                        ),
                        new ShopUnitImport(
                                UUID.randomUUID(),
                                "child offer 2",
                                rootId,
                                ShopUnitType.OFFER,
                                200L
                        ),
                        new ShopUnitImport(
                                UUID.randomUUID(),
                                "child offer 3",
                                rootId,
                                ShopUnitType.OFFER,
                                300L
                        )
                ),
                "2024-01-04T21:00:00.000Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(firstImportItem)))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                        get("/nodes/" + rootId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").isNumber())
                .andExpect(jsonPath("$.price", is(200)));

        var secondImportItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                offerId,
                                "child offer 4",
                                rootId,
                                ShopUnitType.OFFER,
                                1000000L
                        )
                ),
                "2024-01-04T22:00:00.000Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(secondImportItem)))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                get("/sales").param("date", "2024-01-04T22:00:01.000Z"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items[*].name", containsInAnyOrder("child offer 4", "child offer 2","child offer 3")));

        this.mockMvc.perform(
                get("/node/" + rootId + "/statistic"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[*].price", containsInAnyOrder(200, 333500)));

        this.mockMvc.perform(
                get("/node/" + rootId + "/statistic")
                        .param("dateStart", "2024-01-04T22:00:00.000Z")
                        .param("dateEnd", "2024-01-04T23:00:00.000Z"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[*].price", containsInAnyOrder(333500)));

        this.mockMvc.perform(
                        get("/node/" + rootId + "/statistic")
                                .param("dateStart", "2024-01-04")
                                .param("dateEnd", "2024-01-04T23:00:00.000Z"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(validationFailedJson));

    }
}
