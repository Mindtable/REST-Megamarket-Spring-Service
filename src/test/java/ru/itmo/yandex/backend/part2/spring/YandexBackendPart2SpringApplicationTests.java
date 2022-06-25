package ru.itmo.yandex.backend.part2.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.impl.UnsupportedTypeDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.springframework.test.annotation.DirtiesContext.ClassMode;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.itmo.yandex.backend.part2.spring.controller.MegaMarketController;
import ru.itmo.yandex.backend.part2.spring.controller.ShopUnitImport;
import ru.itmo.yandex.backend.part2.spring.controller.ShopUnitImportRequest;
import ru.itmo.yandex.backend.part2.spring.model.ShopUnitType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class YandexBackendPart2SpringApplicationTests {

    public static final
    MediaType APPLICATION_JSON_UTF8 = new MediaType(
                    MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8);

    @Autowired
    private MegaMarketController controller;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itemNotFoundTest() throws Exception {
        // I'm just started writing test
        // I hope I'll end soon...
        // I'm fully committed to my job
        // Good night brothers and sisters...
        this.mockMvc
                .perform(get("/nodes/ba584060-c8f2-3b59-8ce3-f17766ba76d3"))
                .andDo(print())

                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Item not found")));
    }

    @Test
    void itemImportCorrect() throws Exception {
        var testItem = new ShopUnitImport();

        testItem.setId(UUID.nameUUIDFromBytes("00000000-0000-0000-0000-000000000003".getBytes()));
        testItem.setName("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        testItem.setType(ShopUnitType.OFFER);
        testItem.setPrice(1L);


        var testItems = new ArrayList<ShopUnitImport>();
        testItems.add(testItem);

        var test = new ShopUnitImportRequest();
        test.setUpdateDate("2024-01-04T21:00:00.000+07:00");
        test.setItems(testItems);

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();


        this.mockMvc
                .perform(
                        post("/imports")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(ow.writeValueAsString(test)))
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc
                .perform(get("/nodes/" + testItem.getId()))
                .andDo(print())

                .andExpect(status().isOk());
    }

}
