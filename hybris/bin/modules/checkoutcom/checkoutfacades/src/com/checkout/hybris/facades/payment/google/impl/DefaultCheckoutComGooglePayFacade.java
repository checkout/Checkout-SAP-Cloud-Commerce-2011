package com.checkout.hybris.facades.payment.google.impl;

import com.checkout.dto.order.GooglePayMerchantConfiguration;
import com.checkout.hybris.facades.beans.GooglePaySettingsData;
import com.checkout.hybris.facades.merchant.CheckoutComMerchantConfigurationFacade;
import com.checkout.hybris.facades.payment.google.CheckoutComGooglePayFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Default implementation of {@link CheckoutComGooglePayFacade}
 */
public class DefaultCheckoutComGooglePayFacade implements CheckoutComGooglePayFacade {

    protected final CheckoutComMerchantConfigurationFacade checkoutComMerchantConfigurationFacade;
    protected final Converter<GooglePaySettingsData, GooglePayMerchantConfiguration> checkoutComGooglePayPaymentRequestConverter;

    public DefaultCheckoutComGooglePayFacade(final CheckoutComMerchantConfigurationFacade checkoutComMerchantConfigurationFacade,
                                             final Converter<GooglePaySettingsData, GooglePayMerchantConfiguration> checkoutComGooglePayPaymentRequestConverter) {
        this.checkoutComMerchantConfigurationFacade = checkoutComMerchantConfigurationFacade;
        this.checkoutComGooglePayPaymentRequestConverter = checkoutComGooglePayPaymentRequestConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GooglePayMerchantConfiguration getGooglePayMerchantConfiguration() {
        return checkoutComMerchantConfigurationFacade.getGooglePaySettings()
                .map(checkoutComGooglePayPaymentRequestConverter::convert)
                .orElseThrow(() -> new IllegalArgumentException("Google Pay Configuration can not be null"));
    }
}
