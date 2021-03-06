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
import ru.itmo.rest.megamarket.spring.service.model.ShopUnitType;
import ru.itmo.rest.megamarket.spring.service.controller.MegaMarketController;
import ru.itmo.rest.megamarket.spring.service.controller.ShopUnitImport;
import ru.itmo.rest.megamarket.spring.service.controller.ShopUnitImportRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(Config.class)
public class PostMethodTests {
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
    private MockMvc mockMvc;

    private final ObjectWriter ow;

    public PostMethodTests() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        this.ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    public void correctlyRefreshDateAfterUpdate() throws Exception {
        var id = UUID.randomUUID();

        var firstImportItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                id,
                                "???????????????? ?????? ????-????????????",
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
                                "???????????????? ?????? ????-????????????",
                                null,
                                ShopUnitType.OFFER,
                                1L
                        )
                ),
                "2024-01-04T21:00:00.529Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(secondImportItem)))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                get("/nodes/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date", is("2024-01-04T21:00:00.529Z")));

        var thirdImportItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                id,
                                "???????????????? ?????? ????-????????????",
                                null,
                                ShopUnitType.OFFER,
                                1L
                        )
                ),
                "2024-01-05T21:00:00.000Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(thirdImportItem)))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                        get("/nodes/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date", is("2024-01-05T21:00:00.000Z")));
    }

    @Test
    public void changeTypeOfShopUnit() throws Exception {
        var id = UUID.randomUUID();

        var firstImportItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                id,
                                "???????????????? ?????? ????-????????????",
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
                                "???????????????? ?????? ????-????????????",
                                null,
                                ShopUnitType.CATEGORY,
                                null
                        )
                ),
                "2024-01-03T21:00:00.000Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(secondImportItem)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(validationFailedJson));
    }

    @Test
    public void negativePriceOfShopUnit() throws Exception {
        var importItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                UUID.randomUUID(),
                                "???????????????? ?????? ????-????????????",
                                null,
                                ShopUnitType.OFFER,
                                -1L
                        )
                ),
                "2024-01-03T21:00:00.000Z"
        );

        this.mockMvc.perform(
                post("/imports")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(importItem)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(validationFailedJson));
    }

    @Test
    public void nullPriceOfShopUnit() throws Exception {
        var importItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                UUID.randomUUID(),
                                "???????????????? ?????? ????-????????????",
                                null,
                                ShopUnitType.OFFER,
                                null
                        )
                ),
                "2024-01-03T21:00:00.000Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(importItem)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(validationFailedJson));
    }

    @Test
    public void categoryHasPrice() throws Exception {
        var importItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                UUID.randomUUID(),
                                "???????????????? ?????? ????-????????????",
                                null,
                                ShopUnitType.CATEGORY,
                                1L
                        )
                ),
                "2024-01-03T21:00:00.000Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(importItem)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(validationFailedJson));
    }


    @Test
    public void twoItemsWithSameId() throws Exception {
        var id = UUID.randomUUID();

        var importItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                id,
                                "???????????????? ?????? ????-???????????? 1",
                                null,
                                ShopUnitType.OFFER,
                                1L
                        ),
                        new ShopUnitImport(
                                UUID.randomUUID(),
                                "???????????????? ?????? ????-???????????? 2",
                                null,
                                ShopUnitType.OFFER,
                                2L
                        ),
                        new ShopUnitImport(
                                id,
                                "???????????????? ?????? ????-???????????? 3",
                                null,
                                ShopUnitType.OFFER,
                                3L
                        )
                ),
                "2024-01-03T21:00:00.000Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(importItem)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(validationFailedJson));
    }

    @Test
    public void parentIsOffer() throws Exception {
        var id = UUID.randomUUID();

        var importItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                id,
                                "???????????????????????? ??????????????????, ?????????????? ???? ?????????? ???????? ??????????",
                                null,
                                ShopUnitType.OFFER,
                                1L
                        ),
                        new ShopUnitImport(
                                UUID.randomUUID(),
                                "???????????????? ??????????",
                                id,
                                ShopUnitType.OFFER,
                                2L
                        )
                ),
                "2024-01-03T21:00:00.000Z"
        );

        this.mockMvc.perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(importItem)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(validationFailedJson));
    }

    @Test
    public void elementIsNamedNull() throws Exception {
        var id = UUID.randomUUID();

        var importItem = new ShopUnitImportRequest(
                List.of(
                        new ShopUnitImport(
                                id,
                                null,
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
                                .content(ow.writeValueAsString(importItem)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(validationFailedJson));
    }

    @Test
    public void correctyChangeParentOfShopUnit() throws Exception {
        var rootId = UUID.randomUUID();

        var rootShopUnit = new ShopUnitImport(
                rootId,
                "root category",
                null,
                ShopUnitType.CATEGORY,
                null
        );

        var childShopUnit = new ShopUnitImport(
                UUID.randomUUID(),
                "child item",
                rootId,
                ShopUnitType.OFFER,
                1L
        );

        var firstImportItem = new ShopUnitImportRequest(
                List.of(
                        rootShopUnit,
                        childShopUnit
                ),
                "2024-01-03T21:00:00.000Z"
        );

        mockMvc.perform(post("/imports")
                .contentType(APPLICATION_JSON_UTF8)
                .content(ow.writeValueAsString(firstImportItem)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/nodes/" + rootId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children").isArray())
                .andExpect(jsonPath("$.children", hasSize(1)))
                .andExpect(jsonPath("$.id", is(rootId.toString())));

        childShopUnit.setParentId(null);

        var secondImportItem = new ShopUnitImportRequest(
                List.of(
                        rootShopUnit,
                        childShopUnit
                ),
                "2024-01-04T21:00:00.000Z"
        );

        mockMvc.perform(post("/imports")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(ow.writeValueAsString(firstImportItem)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/nodes/" + rootId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children").isArray())
                .andExpect(jsonPath("$.children", hasSize(0)))
                .andExpect(jsonPath("$.id", is(rootId.toString())));
    }
}
