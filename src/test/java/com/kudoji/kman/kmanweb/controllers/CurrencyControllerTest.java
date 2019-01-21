/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.controllers;

import com.kudoji.kman.kmanweb.models.Currency;
import com.kudoji.kman.kmanweb.repositories.CurrencyRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyRepository currencyRepository;

    private Currency currency;

    @Test
    public void testAddNewCurrencyForm() throws Exception{
        mockMvc.perform(get("/currencies/new")).
                andExpect(status().isOk()).
                andExpect(view().name("currency/new")).
                andExpect(model().attribute("currency", new Currency()));
    }

    @Test
    public void testSubmitCurrency() throws Exception{
        when(currencyRepository.save(currency)).thenReturn(currency);

        mockMvc.perform(
                post("/currencies/new")
                        .content("name=currency&code=USD&startsWithCode=1&rate=10")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "/currencies/"));
    }

    @Test
    public void testCurrenciesList() throws Exception{
        List<Currency> currencies = Arrays.asList(
            new Currency("American dollar"),
            new Currency("Canadian dollar")
        );

        when(currencyRepository.findAll()).thenReturn(currencies);

        mockMvc.perform(get("/currencies/"))
                .andExpect(status().isOk())
                .andExpect(view().name("currency/list"))
                .andExpect(model().attribute("currencies", currencies));
    }

    @Test
    public void testEditCurrencyFormWithExistedCurrency() throws Exception{
        Currency currency = new Currency("American dollar");
        currency.setId(1);
        currency.setCode("USD");
        currency.setRate(1.0f);
        currency.setStartsWithCode(true);

        when(currencyRepository.findById(1)).thenReturn(Optional.of(currency));

        mockMvc.perform(get("/currencies/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("currency/new"))
                .andExpect(model().attribute("currency", currency))
                .andExpect(request().attribute("currencyId", 1));
    }

    @Test
    public void testEditCurrencyFormWithNotExistedCurrency() throws Exception{
        when(currencyRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/currencies/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "/currencies/"));
    }

    @Test
    public void testDeleteExistedCurrency() throws Exception{
        Currency currency = new Currency("American dollar");
        currency.setId(1);

        when(currencyRepository.findById(1)).thenReturn(Optional.of(currency));

        mockMvc.perform(get("/currencies/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "/currencies/"))
                .andExpect(flash().attributeExists("infoMessage"));
    }

    @Test
    public void testDeleteNotExistedCurrency() throws Exception{
        when(currencyRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/currencies/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "/currencies/"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}
