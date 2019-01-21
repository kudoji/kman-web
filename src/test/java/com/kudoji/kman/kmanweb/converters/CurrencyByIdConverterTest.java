/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.converters;

import com.kudoji.kman.kmanweb.models.Currency;
import com.kudoji.kman.kmanweb.repositories.CurrencyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CurrencyByIdConverterTest {
    @Autowired
    private ConversionService conversionService;

    @MockBean
    private CurrencyRepository currencyRepository;

    private final Currency currency1 = new Currency("#1");
    private final Currency currency2 = new Currency("#2");

    @Before
    public void setupTest(){
        currency1.setId(1);
        currency2.setId(2);
        when(currencyRepository.findById(1)).thenReturn(Optional.of(currency1));
        when(currencyRepository.findById(2)).thenReturn(Optional.of(currency2));
    }

    @Test
    public void testIntegerToCurrencyConversion(){
        Currency currencyConvert = conversionService.convert("1", Currency.class);
        assertNotNull(currencyConvert);
        assertEquals(currency1, currencyConvert);
        assertNotEquals(currency2, currencyConvert);

        currencyConvert = conversionService.convert("2", Currency.class);
        assertNotNull(currencyConvert);
        assertEquals(currency2, currencyConvert);
        assertNotEquals(currency1, currencyConvert);
    }
}
