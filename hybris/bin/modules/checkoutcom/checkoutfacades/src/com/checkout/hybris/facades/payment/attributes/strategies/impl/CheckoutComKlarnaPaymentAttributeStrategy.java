package com.checkout.hybris.facades.payment.attributes.strategies.impl;

import com.checkout.hybris.core.klarna.session.request.KlarnaSessionRequestDto;
import com.checkout.hybris.core.klarna.session.response.KlarnaSessionResponseDto;
import com.checkout.hybris.core.merchant.services.CheckoutComMerchantConfigurationService;
import com.checkout.hybris.core.model.CheckoutComKlarnaConfigurationModel;
import com.checkout.hybris.core.payment.enums.CheckoutComPaymentType;
import com.checkout.hybris.core.payment.services.CheckoutComPaymentIntegrationService;
import com.checkout.hybris.facades.beans.KlarnaClientTokenData;
import com.checkout.hybris.facades.payment.attributes.mapper.CheckoutComPaymentAttributesStrategyMapper;
import com.checkout.hybris.facades.payment.attributes.strategies.CheckoutComPaymentAttributeStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.Model;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of {@link CheckoutComPaymentAttributeStrategy} for Klarna payment
 */
public class CheckoutComKlarnaPaymentAttributeStrategy extends CheckoutComAbstractPaymentAttributeStrategy {

    protected static final Logger LOG = LogManager.getLogger(CheckoutComKlarnaPaymentAttributeStrategy.class);

    protected static final String KLARNA_CLIENT_TOKEN_MODEL_ATTRIBUTE = "klarnaClientToken";

    protected final CheckoutComPaymentIntegrationService checkoutComPaymentIntegrationService;
    protected final Converter<CartModel, KlarnaSessionRequestDto> checkoutComKlarnaSessionRequestDtoConverter;
    protected final CartService cartService;
    protected final CheckoutComMerchantConfigurationService checkoutComMerchantConfigurationService;

    public CheckoutComKlarnaPaymentAttributeStrategy(final CheckoutComPaymentAttributesStrategyMapper checkoutComPaymentAttributesStrategyMapper,
                                                     final CheckoutComPaymentIntegrationService checkoutComPaymentIntegrationService,
                                                     final Converter<CartModel, KlarnaSessionRequestDto> checkoutComKlarnaSessionRequestDtoConverter,
                                                     final CartService cartService,
                                                     final CheckoutComMerchantConfigurationService checkoutComMerchantConfigurationService) {
        super(checkoutComPaymentAttributesStrategyMapper);
        this.checkoutComPaymentIntegrationService = checkoutComPaymentIntegrationService;
        this.checkoutComKlarnaSessionRequestDtoConverter = checkoutComKlarnaSessionRequestDtoConverter;
        this.cartService = cartService;
        this.checkoutComMerchantConfigurationService = checkoutComMerchantConfigurationService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPaymentAttributeToModel(final Model model) {
        final KlarnaClientTokenData klarnaClientTokenData = new KlarnaClientTokenData();
        klarnaClientTokenData.setSuccess(Boolean.FALSE);

        if (cartService.hasSessionCart()) {
            final CartModel sessionCart = cartService.getSessionCart();
            final KlarnaSessionRequestDto klarnaSessionRequestDto = checkoutComKlarnaSessionRequestDtoConverter.convert(sessionCart);
            try {
                populateKlarnaTokenData(klarnaClientTokenData, checkoutComPaymentIntegrationService.createKlarnaSession(klarnaSessionRequestDto));
            } catch (final ExecutionException e) {
                LOG.error("Error getting the Klarna client token from checkout.com", e);
            }
        }
        model.addAttribute(KLARNA_CLIENT_TOKEN_MODEL_ATTRIBUTE, klarnaClientTokenData);
    }

    /**
     * Populates the data attribute from response and configuration
     *
     * @param klarnaClientTokenData the response to populate
     * @param klarnaSessionResponse the checkout.com create session response
     */
    protected void populateKlarnaTokenData(final KlarnaClientTokenData klarnaClientTokenData, final KlarnaSessionResponseDto klarnaSessionResponse) {
        final CheckoutComKlarnaConfigurationModel klarnaConfiguration = checkoutComMerchantConfigurationService.getKlarnaConfiguration();
        klarnaClientTokenData.setClientToken(klarnaSessionResponse.getClientToken());
        klarnaClientTokenData.setSuccess(Boolean.TRUE);
        klarnaClientTokenData.setInstanceId(klarnaConfiguration.getInstanceId());
        final Set<String> paymentMethodCategories = new HashSet<>();
        klarnaSessionResponse.getPaymentMethodCategories().forEach(paymentMethodCategory ->
                paymentMethodCategories.add(paymentMethodCategory.getIdentifier()));
        klarnaClientTokenData.setPaymentMethodCategories(paymentMethodCategories);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CheckoutComPaymentType getStrategyKey() {
        return CheckoutComPaymentType.KLARNA;
    }
}
