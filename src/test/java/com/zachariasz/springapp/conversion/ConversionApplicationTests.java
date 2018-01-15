package com.zachariasz.springapp.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zachariasz.springapp.conversion.exception.IncorrectInputDataException;
import com.zachariasz.springapp.conversion.message.ExceptionHandlingMessages;
import com.zachariasz.springapp.conversion.model.Exchange;
import com.zachariasz.springapp.conversion.repository.ExchangeRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.core.Is.is;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ConversionApplicationTests {
    private String baseExchange = "PLN";
    private WebApplicationContext webApplicationContext;
    private ExchangeRepository exchangeRepository;
    private List<Exchange> correctExchanges = new ArrayList<>();
    private List<Exchange> incorrectExchanges = new ArrayList<>();


    @Autowired
    public void setWebApplicationContext(WebApplicationContext webApplicationContext, ExchangeRepository exchangeRepository) {
        this.webApplicationContext = webApplicationContext;
        this.exchangeRepository = exchangeRepository;
    }

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Before
    public void addSampleValuesIdCannotBeBaseExchange() throws IncorrectInputDataException {
        // object new Exchange(baseExchange,....) is forbidden in correctExchanges !
        correctExchanges.add(new Exchange("USD", 3.231));
        correctExchanges.add(new Exchange("EUR", 4.135));
        correctExchanges.add(new Exchange("CFK", 2.273));
        correctExchanges.add(new Exchange("RFS", 0.731));

        incorrectExchanges.add(new Exchange("US", 1.0));
        incorrectExchanges.add(new Exchange("US@", 1.0));
        incorrectExchanges.add(new Exchange("U", 1.0));
        incorrectExchanges.add(new Exchange("@", 1.0));
        incorrectExchanges.add(new Exchange("", 1.0));
        incorrectExchanges.add(new Exchange("USAD", 1.0));
        incorrectExchanges.add(new Exchange(null, 1.0));
        incorrectExchanges.add(new Exchange(null, null));
        incorrectExchanges.add(new Exchange("US", 0.0));
        incorrectExchanges.add(new Exchange("US@", 0.0));
        incorrectExchanges.add(new Exchange("U", 0.0));
        incorrectExchanges.add(new Exchange("@", 0.0));
        incorrectExchanges.add(new Exchange("", 0.0));
        incorrectExchanges.add(new Exchange("USAD", 0.0));
        incorrectExchanges.add(new Exchange("USD", null));
        incorrectExchanges.add(new Exchange("USD", 0.0));
        incorrectExchanges.add(new Exchange("PLN", 1.0));
        incorrectExchanges.add(new Exchange());
        incorrectExchanges.add(null);

        addSampleValuesToDatabase();
    }

    @After
    public void removeAllSampleValues() {
        correctExchanges.clear();
        exchangeRepository.deleteAll();
        incorrectExchanges.clear();
    }


    @Test//finished
    public void addExchangeTest() throws Exception {

        for (Exchange correctExchange : correctExchanges) {
            mockMvc.perform(post("/exchanges")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJsonString(correctExchange)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(correctExchange.getId())))
                    .andExpect(jsonPath("$.value", is(correctExchange.getValue())));
        }

        for (Exchange incorrectExchange : incorrectExchanges) {
            mockMvc.perform(post("/exchanges")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJsonString(incorrectExchange)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test//finished
    public void getExchangeTest() throws Exception {
        addSampleValuesToDatabase();
        correctExchanges.add(new Exchange(baseExchange, 1.0));

        for (int i = 0; i < correctExchanges.size(); i++) {
            for (int j = 0; j < correctExchanges.size(); j++) {
                getExchangeTestingMethod(i, j);
            }
        }
    }

    @Test//finished
    public void getAllExchangeTest() throws Exception {

        for (int i = 0; i < correctExchanges.size(); i++) {
            mockMvc.perform(get("/exchanges/all"))
                    .andExpect(
                            status().isOk())
                    .andExpect(
                            content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(
                            jsonPath("$", hasSize(correctExchanges.size())));
        }

    }

    @Test//finished
    public void deleteExchangeTest() throws Exception {

        for (Exchange correctExchange : correctExchanges) {
            mockMvc.perform(delete("/exchanges")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJsonString(correctExchange)))
                    .andExpect(status().isOk());
        }
        for (Exchange correctExchange : correctExchanges) {
            mockMvc.perform(delete("/exchanges")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJsonString(correctExchange)))
                    .andExpect(status().isNotFound());
        }
        for (Exchange incorrectExchange : incorrectExchanges) {
            mockMvc.perform(delete("/exchanges")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(asJsonString(incorrectExchange)))
                    .andExpect(status().isBadRequest());
        }
    }

    //finished
    private void getExchangeTestingMethod(int firstValue, int secondValue) throws Exception {

        mockMvc.perform(get("/exchanges")
                .param("FE", correctExchanges.get(firstValue).getId())
                .param("SE", correctExchanges.get(secondValue).getId())
        ).andExpect(status().isCreated())
                .andExpect(
                        jsonPath(
                                "$.id",
                                is(
                                        correctExchanges.get(firstValue).getId() + "/" + correctExchanges.get(secondValue).getId()
                                )))
                .andExpect(
                        jsonPath(
                                "$.value",
                                is(
                                        firstValue != secondValue ?
                                                BigDecimal.valueOf(correctExchanges.get(firstValue).getValue() / correctExchanges.get(secondValue).getValue())
                                                        .setScale(3, RoundingMode.HALF_UP)
                                                        .doubleValue()
                                                : 1.0
                                )));
    }

    //finished
    private void addSampleValuesToDatabase() throws IncorrectInputDataException {
        for (Exchange correctExchange : correctExchanges) {
            if (correctExchange.getId().equals(baseExchange))
                throw new IncorrectInputDataException(ExceptionHandlingMessages.BASE_EXCHANGE_MESSAGE);
            exchangeRepository.save(correctExchange);
        }
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
