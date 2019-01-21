/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.converters;

import com.kudoji.kman.kmanweb.models.Currency;
import com.kudoji.kman.kmanweb.repositories.CurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CurrencyByIdConverter implements Converter<String, Currency> {
    @Autowired
    private CurrencyRepository currencyRepository;

    @Override
    public Currency convert(String id){
        log.info("Converting id#{}->Currency", id);

        return currencyRepository.findById(Integer.parseInt(id)).orElse(null);
    }
}
