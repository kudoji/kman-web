/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.controllers;

import com.kudoji.kman.kmanweb.models.Currency;
import com.kudoji.kman.kmanweb.repositories.CurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class CurrencyController {
    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping(path = "/currencies/new")
    public String addNewCurrencyForm(Model model){
        model.addAttribute("currency", new Currency());

        return "currency/new";
    }

    @PostMapping(path = "/currencies/new")
    public String submitCurrency(
            @Valid Currency currency,
            Errors errors){

        log.info("Processing currency: '{}'", currency);

        if (errors.hasErrors()){
            log.warn("currency form has {} validation errors for '{}'",
                    errors.getErrorCount(),
                    currency);

            return "currency/new";
        }

        currencyRepository.save(currency);
        log.info("currency saved '{}'", currency);

        return "redirect:/currencies/";
    }

    @GetMapping(path = "/currencies/")
    public String currenciesList(Model model){
        List<Currency> currencies = new ArrayList<>();
        currencyRepository.findAll().forEach((c) -> currencies.add(c));

        model.addAttribute("currencies", currencies);

        return "currency/list";
    }
}
