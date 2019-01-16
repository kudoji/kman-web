/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.controllers;

import com.kudoji.kman.kmanweb.models.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private List<Account> accountList;

    @Before
    public void initialization(){
        accountList = Arrays.asList(
            new Account(1, "Wallet.USD"),
            new Account(2, "Cash.USD")
        );
    }

    @Test
    public void testShowAccounts() throws Exception{
        mockMvc.perform(get("/accounts")).
                andExpect(status().isOk()).
                andExpect(view().name("accounts/accounts")).
                andExpect(model().attribute("accounts", accountList));
    }
}
