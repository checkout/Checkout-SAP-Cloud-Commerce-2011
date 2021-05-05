package com.checkout.hybris.facades.apm.impl;

import com.checkout.hybris.core.apm.services.CheckoutComAPMConfigurationService;
import com.checkout.hybris.core.model.CheckoutComAPMConfigurationModel;
import com.checkout.hybris.facades.apm.CheckoutComAPMConfigurationFacade;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Default implementation of {@link CheckoutComAPMConfigurationFacade}
 */
public class DefaultCheckoutComAPMConfigurationFacade implements CheckoutComAPMConfigurationFacade {

    protected static final String APM_CONFIGURATION_CANNOT_BE_NULL = "APM configuration cannot be null";

    protected final CheckoutComAPMConfigurationService checkoutComAPMConfigurationService;

    public DefaultCheckoutComAPMConfigurationFacade(final CheckoutComAPMConfigurationService checkoutComAPMConfigurationService) {
        this.checkoutComAPMConfigurationService = checkoutComAPMConfigurationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAvailable(final CheckoutComAPMConfigurationModel apmConfiguration, final String countryCode, final String currencyCode) {
        validateParameterNotNull(apmConfiguration, APM_CONFIGURATION_CANNOT_BE_NULL);
        validateParameterNotNull(countryCode, "Country isoCode cannot be null");
        validateParameterNotNull(currencyCode, "Currency isoCode cannot be null");

        return checkoutComAPMConfigurationService.isApmAvailable(apmConfiguration, countryCode, currencyCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRedirect(final CheckoutComAPMConfigurationModel apmConfiguration) {
        validateParameterNotNull(apmConfiguration, APM_CONFIGURATION_CANNOT_BE_NULL);

        return checkoutComAPMConfigurationService.isApmRedirect(apmConfiguration.getCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserDataRequiredRedirect(final CheckoutComAPMConfigurationModel apmConfiguration) {
        validateParameterNotNull(apmConfiguration, APM_CONFIGURATION_CANNOT_BE_NULL);

        return checkoutComAPMConfigurationService.isApmUserDataRequired(apmConfiguration.getCode());
    }
}
