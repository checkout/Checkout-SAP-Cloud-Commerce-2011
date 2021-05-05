package com.checkout.hybris.facades.apm;

import com.checkout.hybris.core.model.CheckoutComAPMConfigurationModel;

/**
 * Handles the apm configuration
 */
public interface CheckoutComAPMConfigurationFacade {

    /**
     * Checks if the apm is available for the given country code and currency code
     *
     * @param apmConfiguration the apm configuration
     * @param countryCode      the country iso code
     * @param currencyCode     the currency iso code
     * @return true if available, false otherwise
     */
    boolean isAvailable(CheckoutComAPMConfigurationModel apmConfiguration, String countryCode, String currencyCode);

    /**
     * Checks if the apm is of redirect type or not
     *
     * @param apmConfiguration the apm configuration
     * @return true if apm is of redirect type, false otherwise
     */
    boolean isRedirect(CheckoutComAPMConfigurationModel apmConfiguration);

    /**
     * Checks if the apm requires manual user data input or not
     *
     * @param apmConfiguration the apm configuration
     * @return true if apm is requires manual user data input, false otherwise
     */
    boolean isUserDataRequiredRedirect(CheckoutComAPMConfigurationModel apmConfiguration);
}
