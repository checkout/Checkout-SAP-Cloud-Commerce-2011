package com.checkout.hybris.addon.validators.paymentform;

import com.checkout.hybris.addon.forms.PaymentDataForm;
import org.springframework.validation.Validator;

import static org.jsoup.helper.StringUtil.isBlank;

/**
 * Abstract validator that implements the validators registration and exposes the get validator key method
 */
public abstract class CheckoutComAbstractPaymentDataFormValidValidator implements Validator {

    /**
     * Checks if the payment form contains the field and whether the value is blank or not
     *
     * @param paymentDataForm payment data form
     * @param fieldName       the field to validate
     * @return true if form doesn't contain the field or it's value is blank, false otherwise
     */
    protected boolean isFieldBlank(final PaymentDataForm paymentDataForm, final String fieldName) {
        return !paymentDataForm.getFormAttributes().containsKey(fieldName) || isBlank((String) paymentDataForm.getFormAttributes().get(fieldName));
    }

}
