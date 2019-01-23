/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.controllers;

import com.kudoji.kman.kmanweb.models.Account;
import com.kudoji.kman.kmanweb.models.Currency;
import com.kudoji.kman.kmanweb.repositories.AccountRepository;
import com.kudoji.kman.kmanweb.repositories.CurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(path = "/accounts")
public class AccountController {
    private final AccountRepository accountRepository;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public AccountController(
            AccountRepository accountRepository,
            CurrencyRepository currencyRepository){
        this.accountRepository = accountRepository;
        this.currencyRepository = currencyRepository;
    }

    @ModelAttribute(value = "currencies")
    public List<Currency> getCurrencies(){
        List<Currency> currencies = new ArrayList<>();
        currencyRepository.findAll().forEach((c) -> currencies.add(c));

        return currencies;
    }

    @ModelAttribute(value = "accounts")
    public List<Account> getAccounts(){
        List<Account> accounts = new ArrayList<>();
        accountRepository.findAll().forEach((a) -> accounts.add(a));

        return accounts;
    }

    @GetMapping
    public String showAccounts(Model model){
        if (getCurrencies().isEmpty()){
            return "redirect:/currencies/new";
        }

        model.addAttribute("account", new Account());

        return "account/accounts";
    }

    /**
     * called when edit/add form action occurs
     * @param account
     * @return
     */
    @PostMapping
    public String submitAccount(
            @Valid Account account,
            Errors errors){

        log.info("Processing account: '{}' with currency '{}'", account, account.getCurrency());

        if (errors.hasErrors()){
            log.warn("account form has {} validation errors for '{}' account", errors.getErrorCount(), account);
            errors.getAllErrors().forEach(e -> log.warn("\terror: '{}'", e.toString()));

            return "account/accounts";
        }

        accountRepository.save(account);
        log.info("account saved '{}'", account);

        return "redirect:/accounts/";
    }
}
