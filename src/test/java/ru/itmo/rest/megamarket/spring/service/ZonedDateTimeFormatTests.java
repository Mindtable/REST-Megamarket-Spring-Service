package ru.itmo.rest.megamarket.spring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.rest.megamarket.spring.service.controller.MegaMarketController;
import ru.itmo.rest.megamarket.spring.service.controller.ShopUnitImport;
import ru.itmo.rest.megamarket.spring.service.controller.ShopUnitImportRequest;
import ru.itmo.rest.megamarket.spring.service.model.ShopUnitType;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ZonedDateTimeFormatTests {
    public static final
    MediaType APPLICATION_JSON_UTF8 = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8);

    @Autowired
    private MegaMarketController controller;

    @Autowired
    private MockMvc mockMvc;

    private final List<ShopUnitImport> testItem;

    private final ObjectWriter ow;

    public ZonedDateTimeFormatTests() {
        this.testItem = new ArrayList<>();

        this.testItem.add(new ShopUnitImport());

        this.testItem.get(0).setPrice(1L);
        this.testItem.get(0).setType(ShopUnitType.OFFER);
        testItem.get(0).setId(UUID.randomUUID());
        testItem.get(0).setName("https://www.youtube.com/watch?v=dQw4w9WgXcQ");

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        this.ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    public void correctIso8601DateWithZLetter() throws Exception {
        ShopUnitImportRequest requestBodyItem = new ShopUnitImportRequest();
        requestBodyItem.setItems(new ArrayList<>(testItem));

        requestBodyItem.setUpdateDate("2024-01-03T21:00:00.000Z");

        this.mockMvc
                .perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(requestBodyItem)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void incorrectIso8601DateWithWrongLetters() throws Exception {
        ShopUnitImportRequest requestBodyItem = new ShopUnitImportRequest();
        requestBodyItem.setItems(new ArrayList<>(testItem));

        requestBodyItem.setUpdateDate("2009-05-19T14a39r");

        this.mockMvc
                .perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(requestBodyItem)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{" +
                        "\"code\":  400," +
                        "\"message\": \"Validation failed\"" +
                        "}"));
    }

    @Test
    public void incorrectIso8601DateWithNumberAsDate() throws Exception {
        ShopUnitImportRequest requestBodyItem = new ShopUnitImportRequest();
        requestBodyItem.setItems(new ArrayList<>(testItem));

        requestBodyItem.setUpdateDate("1056571200000");

        this.mockMvc
                .perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(requestBodyItem)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{" +
                        "\"code\":  400," +
                        "\"message\": \"Validation failed\"" +
                        "}"));
    }

    @Test
    public void correctIso8601DateWithOffset() throws Exception {
        ShopUnitImportRequest requestBodyItem = new ShopUnitImportRequest();
        requestBodyItem.setItems(new ArrayList<>(testItem));

        requestBodyItem.setUpdateDate("2009-05-19T14:39:22-06:00");

        this.mockMvc
                .perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(requestBodyItem)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
