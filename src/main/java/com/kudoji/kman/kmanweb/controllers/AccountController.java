/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.controllers;

import com.kudoji.kman.kmanweb.models.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(path = "/accounts")
public class AccountController {

    @GetMapping
    public String showAccounts(Model model){
        List<Account> accounts = Arrays.asList(
                new Account(1, "Wallet.USD"),
                new Account(2, "Cash.USD")
        );

        model.addAttribute("accounts", accounts);

        return "accounts/accounts";
    }
}
