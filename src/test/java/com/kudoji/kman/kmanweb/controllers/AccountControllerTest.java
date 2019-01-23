/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.controllers;

import com.kudoji.kman.kmanweb.models.Account;
import com.kudoji.kman.kmanweb.models.Currency;
import com.kudoji.kman.kmanweb.repositories.AccountRepository;
import com.kudoji.kman.kmanweb.repositories.CurrencyRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private CurrencyRepository currencyRepository;

    private List<Account> accountList;
    private List<Currency> currencyList;

    @Before
    public void initialization(){
        Currency currencyUSD = new Currency("American Dollar");
        currencyUSD.setId(1);
        currencyUSD.setCode("USD");

        currencyList = Arrays.asList(
                currencyUSD,
                new Currency("Canadian Dollar")
        );

        accountList = Arrays.asList(
            new Account(currencyUSD, 10f),
            new Account()
        );

        when(accountRepository.findAll()).thenReturn(accountList);
        when(currencyRepository.findAll()).thenReturn(currencyList);
    }

    @Test
    public void testShowAccountsWithNoCurrencies() throws Exception{
        when(currencyRepository.findAll()).thenReturn(new ArrayList<Currency>());

        mockMvc.perform(get("/accounts"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "/currencies/new"));
    }

    @Test
    public void testShowAccountsWithCurrencies() throws Exception{
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/accounts"))
                .andExpect(model().attribute("accounts", accountList))
                .andExpect(model().attribute("currencies", currencyList))
                .andExpect(model().attributeExists("account"));
    }

    @Test
    public void testSubmitAccount() throws Exception{
        Account account = accountList.get(0);
        when(accountRepository.save(account)).thenReturn(account);
        when(currencyRepository.findById(1)).thenReturn(Optional.of(currencyList.get(0)));

        mockMvc.perform(
                    post("/accounts")
                    .content("name=dollar&balanceInitial=10&currency=1")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues("Location", "/accounts/"));
    }

    @Test
    public void testSubmitAccountWithErrors() throws Exception{
        Account account = accountList.get(0);
        when(accountRepository.save(account)).thenReturn(account);
        when(currencyRepository.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(
                post("/accounts")
                        .content("name=dollar&balanceInitial=10&currency=1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("account/accounts"))
                .andExpect(model().hasErrors());
    }
}
