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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class CurrencyController {
    @Autowired
    private CurrencyRepository currencyRepository;

    @ModelAttribute(name = "currencies")
    private List<Currency> getCurrencies(){
        List<Currency> currencies = new ArrayList<>();
        currencyRepository.findAll().forEach((c) -> currencies.add(c));

        return currencies;
    }

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
        //  check getCurrencies() method

        return "currency/list";
    }

    @GetMapping(path = "/currencies/edit/{currencyId:[\\d]+}")
    public String editCurrencyForm(
            Model model,
            @PathVariable int currencyId,
            RedirectAttributes redirectAttributes){
        log.info("processing edit form for currency #{}", currencyId);

        Optional<Currency> optionalCurrency = currencyRepository.findById(currencyId);
        if (! optionalCurrency.isPresent()){
            String error = String.format("currency #%d doesn't exist", currencyId);
            log.warn(error);

            redirectAttributes.addFlashAttribute("errorMessage", error);

            return "redirect:/currencies/";
        }


        model.addAttribute("currency", optionalCurrency.get());

        return "currency/new";
    }
}
