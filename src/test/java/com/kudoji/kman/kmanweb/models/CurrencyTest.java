/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.models;

import com.kudoji.kman.kmanweb.repositories.CurrencyRepository;
import com.kudoji.kman.kmanweb.testutils.AssertValidation;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.*;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CurrencyTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CurrencyRepository currencyRepository;

    private static Validator validator;
    private static AssertValidation<Currency> currencyAssertValidation;

    @BeforeClass
    public static void initialization(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();

        currencyAssertValidation = new AssertValidation<>(validator);
    }

    @Test
    public void testCurrencyPersistence(){
        String usdName = "American dollars";
        String usdCode = "USD";

        Currency usd = new Currency(usdName);
        usd.setCode(usdCode);
        usd.setStartsWithCode(true);
        usd.setRate(1f);

        testEntityManager.persist(usd);
        testEntityManager.flush();

        assertEquals(1, currencyRepository.count());

        Currency found = currencyRepository.findByCode(usdCode);
        assertEquals(usdName, found.getName());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testCurencyCodeUniqueness(){
        String usdName = "American dollars";
        String usdCode = "USD";

        Currency usd = new Currency(usdName);
        usd.setCode(usdCode);
        usd.setStartsWithCode(true);
        usd.setRate(1f);

        Currency usd1 = new Currency(usdName);
        usd.setCode(usdCode);
        usd.setStartsWithCode(false);
        usd.setRate(12);

        testEntityManager.persist(usd);
        testEntityManager.persist(usd1);
        testEntityManager.flush();
    }

    @Test
    public void testNameValidation(){
        Currency currency = new Currency(null);
        currencyAssertValidation.assertErrorValidation(currency, "name", "Name is invalid");

        currency.setName("");
        currencyAssertValidation.assertErrorValidation(currency, "name", "Name must be from 5 to 35 characters long");

        currency.setName("1234");
        currencyAssertValidation.assertErrorValidation(currency, "name", "Name must be from 5 to 35 characters long");

        currency.setName("12345");
        currencyAssertValidation.assertNoErrorValidation(currency, "name");

        String name35 = Stream.generate(() -> 1).
                limit(35).
                map(integer -> integer.toString()).
                collect(Collectors.joining(""));
        currency.setName(name35);
        currencyAssertValidation.assertNoErrorValidation(currency, "name");

        String name36 = Stream.generate(() -> 1).
                limit(36).
                map(integer -> integer.toString()).
                collect(Collectors.joining(""));
        currency.setName(name36);
        currencyAssertValidation.assertErrorValidation(currency, "name", "Name must be from 5 to 35 characters long");
    }

    @Test
    public void testCodeValidation(){
        Currency currency = new Currency("American dollar");
        currencyAssertValidation.assertErrorValidation(currency, "code", "Currency code is not set");

        currency.setCode(null);
        currencyAssertValidation.assertErrorValidation(currency, "code", "Currency code is not set");

        currency.setCode("");
        currencyAssertValidation.assertErrorValidation(currency, "code", "Code must be from 2 to 5 characters long");

        currency.setCode("1");
        currencyAssertValidation.assertErrorValidation(currency, "code", "Code must be from 2 to 5 characters long");

        currency.setCode("11");
        currencyAssertValidation.assertNoErrorValidation(currency, "code");

        currency.setCode(Stream.generate(() -> 1).
                limit(5).
                map((integer) -> integer.toString()).
                collect(Collectors.joining(""))
        );
        currencyAssertValidation.assertNoErrorValidation(currency, "code");

        currency.setCode(Stream.generate(() -> 1).
                limit(6).
                map((integer) -> integer.toString()).
                collect(Collectors.joining(""))
        );
        currencyAssertValidation.assertErrorValidation(currency, "code", "Code must be from 2 to 5 characters long");
    }

    @Test
    public void testRateValidation(){
        Currency currency = new Currency("American dollar");
        currencyAssertValidation.assertNoErrorValidation(currency, "rate");

        currency.setRate(0f);
        currencyAssertValidation.assertNoErrorValidation(currency, "rate");

        currency.setRate(-1f);
        currencyAssertValidation.assertErrorValidation(currency, "rate", "Rate must be more than 0.0");

        currency.setRate(10f);
        currencyAssertValidation.assertNoErrorValidation(currency, "rate");
    }
}
