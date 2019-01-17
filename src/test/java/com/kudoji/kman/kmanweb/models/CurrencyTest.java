/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.models;

import com.kudoji.kman.kmanweb.repositories.CurrencyRepository;
import com.kudoji.kman.kmanweb.testutils.AssertValidation;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.*;

import static com.kudoji.kman.kmanweb.testutils.AssertValidation.getString;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CurrencyTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CurrencyRepository currencyRepository;

    private static AssertValidation<Currency> currencyAssertValidation;

    @BeforeClass
    public static void initialization(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        currencyAssertValidation = new AssertValidation<>(validator);
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

        String name35 = getString(35);
        currency.setName(name35);
        currencyAssertValidation.assertNoErrorValidation(currency, "name");

        String name36 = getString(36);
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

        currency.setCode(getString(5));
        currencyAssertValidation.assertNoErrorValidation(currency, "code");

        currency.setCode(getString(6));
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

        Integer currecyId = testEntityManager.getId(usd, Integer.class);
        assertNotNull(currecyId);
        assertTrue(currecyId > 0);

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
}
