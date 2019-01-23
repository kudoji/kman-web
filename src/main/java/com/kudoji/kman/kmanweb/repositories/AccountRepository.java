/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.repositories;

import com.kudoji.kman.kmanweb.models.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Integer> {
}
