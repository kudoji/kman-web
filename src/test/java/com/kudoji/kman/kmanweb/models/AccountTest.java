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
        String accountName1 = "Wallet.USD";
        String accountName2 = "Bank.USD";
        Currency currency = new Currency("American dollar");
        currency.setCode("USD");

        Account account1 = new Account(currency, 0f);
        account1.setName(accountName1);

        Account account2 = new Account(currency, 1000f);
        account2.setName(accountName2);

        assertEquals(2, currency.getAccounts().size());
        assertTrue(currency.getAccounts().contains(account1));
        assertTrue(currency.getAccounts().contains(account2));
        assertEquals(currency, account1.getCurrency());
        assertEquals(currency, account2.getCurrency());

        testEntityManager.persistAndFlush(currency);

        Integer currencyId = testEntityManager.getId(currency, Integer.class);
        assertNotNull(currencyId);
        assertTrue(currencyId > 0);

        Integer accountId1 = testEntityManager.getId(account1, Integer.class);
        assertNotNull(accountId1);
        assertTrue(accountId1 > 0);

        Account accountTest = testEntityManager.find(Account.class, accountId1);
        assertEquals(accountName1, accountTest.getName());

        Integer accountId2 = testEntityManager.getId(account2, Integer.class);
        assertNotNull(accountId2);
        assertTrue(accountId2 > 0);

        accountTest = testEntityManager.find(Account.class, accountId2);
        assertEquals(accountName2, accountTest.getName());
    }

    @Test
    public void testSetCurrency(){
        Currency currency = new Currency("American dollar");
        currency.setCode("USD");

        assertEquals(0, currency.getAccounts().size());

        Account account1 = new Account();
        account1.setCurrency(currency);
        account1.setName("wallet.usd");

        assertEquals(1, currency.getAccounts().size());

        Account account2 = new Account();
        account2.setCurrency(currency);
        account2.setName("bank.usd");

        assertEquals(2, currency.getAccounts().size());
        assertTrue(currency.getAccounts().contains(account1));
        assertTrue(currency.getAccounts().contains(account2));
        assertEquals(currency, account1.getCurrency());
        assertEquals(currency, account2.getCurrency());

        account1.setCurrency(null);
        assertEquals(1, currency.getAccounts().size());
        assertTrue(!currency.getAccounts().contains(account1));
        assertTrue(currency.getAccounts().contains(account2));
        assertEquals(null, account1.getCurrency());
        assertEquals(currency, account2.getCurrency());

        account2.setCurrency(null);
        assertEquals(0, currency.getAccounts().size());
        assertEquals(null, account1.getCurrency());
        assertEquals(null, account2.getCurrency());
    }

    @Test
    public void testPreDelete(){
        Currency currency = new Currency("American dollar");
        currency.setCode("USD");

        Account account1 = new Account(currency, 0f);
        account1.setName("wallet.usd");

        Account account2 = new Account(currency, 1f);
        account2.setName("bank.usd");

        testEntityManager.persistAndFlush(currency);

        Integer currencyId = testEntityManager.getId(currency, Integer.class);
        assertTrue(currencyId > 0);

        Integer accountId1 = testEntityManager.getId(account1, Integer.class);
        assertTrue(accountId1 > 0);

        Integer accountId2 = testEntityManager.getId(account2, Integer.class);
        assertTrue(accountId2 > 0);

        assertEquals(currency, account1.getCurrency());
        assertEquals(currency, account2.getCurrency());
        assertEquals(2, currency.getAccounts().size());

        testEntityManager.remove(account1);

        assertEquals(null, account1.getCurrency());
        assertEquals(currency, account2.getCurrency());
        assertEquals(1, currency.getAccounts().size());
        assertTrue(!currency.getAccounts().contains(account1));
        assertTrue(currency.getAccounts().contains(account2));
    }
}
