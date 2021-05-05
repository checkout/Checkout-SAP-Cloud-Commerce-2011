package com.checkout.hybris.addon.controllers.payment;

import com.checkout.hybris.core.payment.exception.CheckoutComPaymentIntegrationException;
import com.checkout.hybris.facades.accelerator.CheckoutComCheckoutFlowFacade;
import com.checkout.hybris.facades.beans.AuthorizeResponseData;
import com.checkout.hybris.facades.beans.PlaceWalletOrderDataResponse;
import com.checkout.hybris.facades.beans.WalletPaymentAdditionalAuthInfo;
import com.checkout.hybris.facades.beans.WalletPaymentInfoData;
import com.checkout.hybris.facades.enums.PlaceWalletOrderStatus;
import com.checkout.hybris.facades.enums.WalletPaymentType;
import com.checkout.hybris.facades.payment.CheckoutComPaymentFacade;
import com.checkout.hybris.facades.payment.CheckoutComPaymentInfoFacade;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.order.InvalidCartException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;

import javax.annotation.Resource;

/**
 * Abstract controller to handle the common logic between different Wallet payment methods
 */
public abstract class CheckoutComAbstractWalletPaymentController extends AbstractCheckoutController {

    protected static final Logger LOG = LogManager.getLogger(CheckoutComAbstractWalletPaymentController.class);

    @Resource
    protected CheckoutComPaymentFacade checkoutComPaymentFacade;
    @Resource
    protected MessageSource messageSource;
    @Resource
    protected CheckoutComPaymentInfoFacade checkoutComPaymentInfoFacade;
    @Resource
    protected CheckoutComCheckoutFlowFacade checkoutFlowFacade;

    /**
     * Places the order for the given wallet payment type
     *
     * @param walletPaymentAdditionalAuthInfo the create token request object
     * @param walletPaymentType               the wallet specific type
     * @return the populated place order response
     */
    protected PlaceWalletOrderDataResponse placeWalletOrder(final WalletPaymentAdditionalAuthInfo walletPaymentAdditionalAuthInfo,
                                                            final WalletPaymentType walletPaymentType) {

        final PlaceWalletOrderDataResponse response = new PlaceWalletOrderDataResponse();

        WalletPaymentInfoData paymentInfoData = null;
        try {
            paymentInfoData = checkoutComPaymentFacade.createCheckoutComWalletPaymentToken(walletPaymentAdditionalAuthInfo, walletPaymentType);
        } catch (final CheckoutComPaymentIntegrationException e) {
            LOG.error("Exception when trying to get the [{}] request token from checkout.com", walletPaymentType.name(), e);
            return handleFailureProcess(response, messageSource.getMessage("checkout.error.authorization.failed", null, getI18nService().getCurrentLocale()));
        }

        checkoutComPaymentInfoFacade.addPaymentInfoToCart(paymentInfoData);
        final AuthorizeResponseData authorizeResponseData = checkoutFlowFacade.authorizePayment();

        if (!authorizeResponseData.getIsSuccess()) {
            LOG.error("Error with the authorization process. Redirecting to payment method step.");
            return handleFailureProcess(response, messageSource.getMessage("checkout.error.authorization.failed", null, getI18nService().getCurrentLocale()));
        }

        final OrderData orderData;
        try {
            orderData = checkoutFlowFacade.placeOrder();
        } catch (final InvalidCartException e) {
            LOG.error("Failed to place Order", e);
            return handleFailureProcess(response, messageSource.getMessage("checkout.placeOrder.failed", null, getI18nService().getCurrentLocale()));
        }

        response.setStatus(PlaceWalletOrderStatus.SUCCESS);
        response.setOrderData(orderData);
        return response;
    }

    /**
     * Handles the wallet failure scenario
     *
     * @param response         the place order response
     * @param errorMessageCode the specific error message for the
     * @return the response populated for the given error
     */
    protected PlaceWalletOrderDataResponse handleFailureProcess(final PlaceWalletOrderDataResponse response, final String errorMessageCode) {
        checkoutFlowFacade.removePaymentInfoFromSessionCart();
        response.setStatus(PlaceWalletOrderStatus.FAILURE);
        response.setErrorMessage(errorMessageCode);
        return response;
    }
}
