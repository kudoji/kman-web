/**
 * @author kudoji
 */
package com.kudoji.kman.kmanweb.testutils;

import com.kudoji.kman.kmanweb.models.Currency;
import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@Data
public class AssertValidation<T> {
    private final Validator validator;

    public void assertErrorValidation(T t, String property, String errorMessage){
        Set<ConstraintViolation<T>> constraintViolations =
                validator.validateProperty(t, property);

        assertEquals(1, constraintViolations.size());
        assertEquals(errorMessage, constraintViolations.iterator().next().getMessage());
    }

    public void assertNoErrorValidation(T t, String property){
        Set<ConstraintViolation<T>> constraintViolations =
                validator.validateProperty(t, property);

        assertEquals(0, constraintViolations.size());
    }

}
