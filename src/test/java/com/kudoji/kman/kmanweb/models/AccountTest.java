/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.models;

import com.kudoji.kman.kmanweb.repositories.AccountRepository;
import com.kudoji.kman.kmanweb.testutils.AssertValidation;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static com.kudoji.kman.kmanweb.testutils.AssertValidation.getString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AccountTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AccountRepository accountRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static AssertValidation<Account> assertValidation;

    @BeforeClass
    public static void initialization(){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        assertValidation = new AssertValidation<>(validator);
    }

    @Test
    public void testConstructorWithInvalidCurrency(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Currency cannot be null");

        new Account(null, 0f);
    }

    @Test
    public void testConstructorWithInvalidBalance(){
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Initial balance cannot be less than zero");

        new Account(new Currency("American dollar"), -10f);
    }

    @Test
    public void testNameValidation(){
        Currency currency = new Currency("American dollar");

        Account account = new Account(currency, 0f);
        assertValidation.assertErrorValidation(account, "name", "Name is invalid");

        account.setName("");
        assertValidation.assertErrorValidation(account, "name", "Name must be from 5 to 35 characters long");

        account.setName(getString(4));
        assertValidation.assertErrorValidation(account, "name", "Name must be from 5 to 35 characters long");

        account.setName(getString(5));
        assertValidation.assertNoErrorValidation(account, "name");

        account.setName(getString(35));
        assertValidation.assertNoErrorValidation(account, "name");

        account.setName(getString(36));
        assertValidation.assertErrorValidation(account, "name", "Name must be from 5 to 35 characters long");
    }

    @Test
    public void testBalancesValidation(){
        Currency currency = new Currency("American dollar");

        Account account = new Account(currency, 0f);
        account.setName("Wallet.USD");
        assertValidation.assertNoErrorValidation(account, "balanceInitial");
        assertValidation.assertNoErrorValidation(account, "balanceCurrent");

        account.setBalanceCurrent(-1f);
        assertValidation.assertErrorValidation(account, "balanceCurrent", "Current balance cannot be less than zero");
    }

    @Test
    public void testCascadePersistence(){
        String accountName = "Wallet.USD";
        Currency currency = new Currency("American dollar");
        currency.setCode("USD");

        Account account = new Account(currency, 0f);
        account.setName(accountName);

        testEntityManager.persist(currency);
        testEntityManager.flush();

        Integer currencyId = testEntityManager.getId(currency, Integer.class);
        assertNotNull(currencyId);
        assertTrue(currencyId > 0);

        Integer accountId = testEntityManager.getId(account, Integer.class);
        assertNotNull(accountId);
        assertTrue(accountId > 0);

        Account account1 = testEntityManager.find(Account.class, accountId);
        assertEquals(accountName, account1.getName());
    }
}
