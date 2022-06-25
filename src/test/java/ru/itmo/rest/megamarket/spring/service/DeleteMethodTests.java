package ru.itmo.rest.megamarket.spring.service;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.rest.megamarket.spring.service.configuration.Config;
import ru.itmo.rest.megamarket.spring.service.controller.MegaMarketController;
import ru.itmo.rest.megamarket.spring.service.controller.ShopUnitImport;
import ru.itmo.rest.megamarket.spring.service.controller.ShopUnitImportRequest;
import ru.itmo.rest.megamarket.spring.service.model.ShopUnitType;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(Config.class)
public class DeleteMethodTests {

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

    public DeleteMethodTests() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        this.ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    public void correctlyDeleteSingleElement() throws Exception{
        var id = UUID.randomUUID();

        var firstImportItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                id,
                                "тестовое имя по-русски",
                                null,
                                ShopUnitType.OFFER,
                                1L
                        )
                ),
                "2024-01-03T21:00:00.000Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(firstImportItem)))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                delete("/delete/" + id))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                get("/nodes/" + id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json(itemNotFoundJson));
    }

    @Test
    public void correctlyDeleteChildElement() throws Exception{
        var rootId = UUID.randomUUID();
        var childOfferId = UUID.randomUUID();
        var childChildOfferId = UUID.randomUUID();
        var childCategoryId = UUID.randomUUID();

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
                                childOfferId,
                                "child offer",
                                rootId,
                                ShopUnitType.OFFER,
                                1L
                        ),
                        new ShopUnitImport(
                                childCategoryId,
                                "child category",
                                rootId,
                                ShopUnitType.CATEGORY,
                                null
                        ),
                        new ShopUnitImport(
                                childChildOfferId,
                                "child child offer",
                                childCategoryId,
                                ShopUnitType.OFFER,
                                1L
                        )
                ),
                "2024-01-03T21:00:00.000Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(firstImportItem)))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                        delete("/delete/" + rootId))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                        get("/nodes/" + rootId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json(itemNotFoundJson));
        this.mockMvc.perform(
                        get("/nodes/" + childOfferId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json(itemNotFoundJson));
        this.mockMvc.perform(
                        get("/nodes/" + childChildOfferId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json(itemNotFoundJson));
        this.mockMvc.perform(
                        get("/nodes/" + childCategoryId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json(itemNotFoundJson));
    }

    @Test
    public void correctlyDeleteSingleElementAndHisStatistics() throws Exception{
        var id = UUID.randomUUID();

        var firstImportItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                id,
                                "тестовое имя по-русски",
                                null,
                                ShopUnitType.OFFER,
                                1L
                        )
                ),
                "2024-01-03T21:00:00.000Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(firstImportItem)))
                .andDo(print())
                .andExpect(status().isOk());

        var secondImportItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                id,
                                "тестовое имя по-русски eще раз",
                                null,
                                ShopUnitType.OFFER,
                                1L
                        )
                ),
                "2024-01-04T21:00:00.000Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(secondImportItem)))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                get("/node/" + id + "/statistic"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(content().string(containsString("2024-01-04T21:00:00.000Z")))
                .andExpect(content().string(containsString("2024-01-03T21:00:00.000Z")));

        this.mockMvc.perform(
                        delete("/delete/" + id))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                        get("/nodes/" + id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json(itemNotFoundJson));

        this.mockMvc.perform(
                        get("/node/" + id + "/statistic"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json(itemNotFoundJson));
    }

    @Test
    public void itemNotFound() throws Exception {
        this.mockMvc.perform(
                delete("/delete/" + UUID.randomUUID()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().json(itemNotFoundJson));
    }

    @Test
    public void incorrectUUIDPassed() throws Exception {
        this.mockMvc.perform(
                        delete("/delete/" + "123e4567-q89b-12d3-a456-426655440000"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(validationFailedJson));
    }
}
