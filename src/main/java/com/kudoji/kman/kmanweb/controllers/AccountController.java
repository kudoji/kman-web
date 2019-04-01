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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        if (account.getId() == 0){
            //  insert
            accountRepository.save(account);
        }else{
            //  update
            currencyRepository.save(account.getCurrency());
            //  even cascade set to PERSIST for currency, the currency might not be changed
            //  this is why calling additional save(account) is needed.
            //  at the same time save(currency) is important because it removes flag DETACHED
            //  (which happens due to calling account.setId() and hibernate thinks that
            //  id is set manually and cannot be generated)
            // from the account entity.
            accountRepository.save(account);
        }

        log.info("account saved '{}'", account);

        return "redirect:/accounts/";
    }

    @GetMapping(path = "/edit/{accountId:[\\d]+}")
    public String editAccountForm(
            Model model,
            @PathVariable(value = "accountId") int accountId,
            RedirectAttributes redirectAttributes){
        log.info("processing edit form for account #{}", accountId);

        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (! optionalAccount.isPresent()){
            String error = String.format("account #%d doesn't exist", accountId);
            log.warn(error);

            redirectAttributes.addFlashAttribute("errorMessage", error);

            return "redirect:/accounts/";
        }


        model.addAttribute("account", optionalAccount.get());

        return "account/accounts";
    }

    @GetMapping(path = "/delete/{accountId:[\\d]+}")
    public String deleteAccountForm(
            @PathVariable int accountId,
            RedirectAttributes redirectAttributes){
        log.info("processing delete form for account #{}", accountId);

        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (! optionalAccount.isPresent()){
            String error = String.format("account #%d doesn't exist", accountId);
            log.warn(error);

            redirectAttributes.addFlashAttribute("errorMessage", error);

            return "redirect:/accounts/";
        }

        accountRepository.delete(optionalAccount.get());

        String infoMessage = String.format("account#%d were successfully deleted", accountId);
        redirectAttributes.addFlashAttribute("infoMessage", infoMessage);
        log.info(infoMessage);

        return "redirect:/accounts/";
    }
}
