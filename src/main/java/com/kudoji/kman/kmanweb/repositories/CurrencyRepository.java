/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.repositories;

import com.kudoji.kman.kmanweb.models.Currency;
import org.springframework.data.repository.CrudRepository;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
    Currency findByCode(String code);
}
