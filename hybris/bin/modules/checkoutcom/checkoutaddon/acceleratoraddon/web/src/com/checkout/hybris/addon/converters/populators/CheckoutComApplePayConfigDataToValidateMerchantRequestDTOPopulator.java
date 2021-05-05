package com.checkout.hybris.addon.converters.populators;

import com.checkout.hybris.core.url.services.CheckoutComUrlService;
import com.checkout.hybris.facades.beans.ApplePaySettingsData;
import com.checkout.hybris.facades.beans.ApplePayValidateMerchantData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Populates from ApplePaySettingsData to ApplePayValidateMerchantData
 */
public class CheckoutComApplePayConfigDataToValidateMerchantRequestDTOPopulator implements Populator<ApplePaySettingsData, ApplePayValidateMerchantData> {

    protected static final Logger LOG = LogManager.getLogger(CheckoutComApplePayConfigDataToValidateMerchantRequestDTOPopulator.class);

    protected static final String WEB = "web";

    protected final CheckoutComUrlService checkoutComUrlService;

    public CheckoutComApplePayConfigDataToValidateMerchantRequestDTOPopulator(final CheckoutComUrlService checkoutComUrlService) {
        this.checkoutComUrlService = checkoutComUrlService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final ApplePaySettingsData source, final ApplePayValidateMerchantData target) throws ConversionException {
        Assert.notNull(source, "Source ApplePaySettingsData cannot be null.");
        Assert.notNull(target, "Target ApplePayValidateMerchantData cannot be null.");

        target.setMerchantIdentifier(source.getMerchantId());
        target.setDisplayName(source.getMerchantName());
        target.setInitiative(WEB);
        final String currentWebSiteURL = checkoutComUrlService.getWebsiteUrlForCurrentSite();
        try {
            final URL url = new URL(currentWebSiteURL);
            target.setInitiativeContext(url.getHost());
        } catch (final MalformedURLException e) {
            LOG.error("Malformed URL {}", currentWebSiteURL, e);
            throw new ConversionException(String.format("Error converting from ApplePaySettingsData to ApplePayValidateMerchantData. Malformed URL [%s]", currentWebSiteURL), e);
        }
    }
}