package com.checkout.hybris.core.apm.services.impl;

import com.checkout.hybris.core.apm.configuration.CheckoutComAPMConfigurationSettings;
import com.checkout.hybris.core.apm.services.CheckoutComAPMConfigurationService;
import com.checkout.hybris.core.model.CheckoutComAPMConfigurationModel;
import com.google.common.collect.ImmutableMap;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Default implementation of {@link CheckoutComAPMConfigurationService}
 */
public class DefaultCheckoutComAPMConfigurationService implements CheckoutComAPMConfigurationService {

    protected static final Logger LOG = LogManager.getLogger(DefaultCheckoutComAPMConfigurationService.class);
    protected static final String APM_CONFIGURATION_CODE_CANNOT_BE_NULL = "APM configuration code cannot be null.";

    protected final GenericDao<CheckoutComAPMConfigurationModel> checkoutComApmConfigurationDao;
    protected final Map<String, CheckoutComAPMConfigurationSettings> checkoutComAPMConfigurationSettings;

    public DefaultCheckoutComAPMConfigurationService(final GenericDao<CheckoutComAPMConfigurationModel> checkoutComApmConfigurationDao, final Map<String, CheckoutComAPMConfigurationSettings> checkoutComAPMConfigurationSettings) {
        this.checkoutComApmConfigurationDao = checkoutComApmConfigurationDao;
        this.checkoutComAPMConfigurationSettings = checkoutComAPMConfigurationSettings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApmAvailable(final CheckoutComAPMConfigurationModel apmConfiguration, final String countryCode, final String currencyCode) {
        checkArgument(isNotBlank(countryCode), "Country code cannot be null");
        checkArgument(isNotBlank(currencyCode), "Currency code cannot be null");

        if (apmConfiguration == null) {
            LOG.warn("The apm is not defined, the apm component is not restricted.");
            return true;
        }

        final boolean countryMatch = isEmpty(apmConfiguration.getRestrictedCountries()) ||
                apmConfiguration.getRestrictedCountries().stream().anyMatch(country -> countryCode.equalsIgnoreCase(country.getIsocode()));

        final boolean currencyMatch = isEmpty(apmConfiguration.getRestrictedCurrencies()) ||
                apmConfiguration.getRestrictedCurrencies().stream().anyMatch(currency -> currencyCode.equalsIgnoreCase(currency.getIsocode()));

        return countryMatch && currencyMatch;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CheckoutComAPMConfigurationModel> getApmConfigurationByCode(final String apmCode) {
        checkArgument(isNotBlank(apmCode), APM_CONFIGURATION_CODE_CANNOT_BE_NULL);

        final List<CheckoutComAPMConfigurationModel> searchResults = checkoutComApmConfigurationDao.find(ImmutableMap.of(CheckoutComAPMConfigurationModel.CODE, apmCode));

        return CollectionUtils.isNotEmpty(searchResults) ? Optional.of(searchResults.get(0)) : Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApmRedirect(final String apmCode) {
        checkArgument(isNotBlank(apmCode), APM_CONFIGURATION_CODE_CANNOT_BE_NULL);
        checkArgument(checkoutComAPMConfigurationSettings.containsKey(apmCode) && checkoutComAPMConfigurationSettings.get(apmCode) != null, "There is no setting for the APM configuration code");

        return checkoutComAPMConfigurationSettings.get(apmCode).getIsApmRedirect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApmUserDataRequired(final String apmCode) {
        checkArgument(isNotBlank(apmCode), APM_CONFIGURATION_CODE_CANNOT_BE_NULL);
        checkArgument(checkoutComAPMConfigurationSettings.containsKey(apmCode) && checkoutComAPMConfigurationSettings.get(apmCode) != null, "There is no setting for the APM configuration code");

        return checkoutComAPMConfigurationSettings.get(apmCode).getIsApmUserDataRequired();
    }
}
