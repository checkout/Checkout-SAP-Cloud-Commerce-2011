package com.checkout.hybris.addon.controllers.payment;

import com.checkout.hybris.facades.address.CheckoutComAddressFacade;
import com.checkout.hybris.facades.beans.*;
import com.checkout.hybris.facades.enums.WalletPaymentType;
import com.checkout.hybris.facades.merchant.CheckoutComMerchantConfigurationFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
@RequestMapping(value = "/checkout/payment/checkout-com/applepay")
public class CheckoutComApplePayController extends CheckoutComAbstractWalletPaymentController {

    @Resource
    protected CheckoutComMerchantConfigurationFacade checkoutComMerchantConfigurationFacade;
    @Resource
    protected Converter<ApplePaySettingsData, ApplePayValidateMerchantData> checkoutComApplePayConfigDataToValidateMerchantRequestDTOPopulatingConverter;
    @Resource
    protected Converter<ApplePayPaymentContact, AddressData> checkoutComApplePayAddressReverseConverter;
    @Resource
    protected CheckoutComAddressFacade checkoutComAddressFacade;
    @Resource
    protected CheckoutCustomerStrategy checkoutCustomerStrategy;

    /**
     * Validates the session for apple pay
     *
     * @param validateMerchantRequestData the validate session request
     * @return the response
     */
    @PostMapping(value = "/request-session", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public Object requestPaymentSession(@RequestBody final ApplePayValidateMerchantRequestData validateMerchantRequestData) {
        final Optional<ApplePayValidateMerchantData> validateMerchantDataOptional = getValidateMerchantData();

        final ApplePayValidateMerchantData validateMerchantData = validateMerchantDataOptional.orElse(null);

        final SSLConnectionSocketFactory applePayConnectionFactory = checkoutComPaymentFacade.createApplePayConnectionFactory();

        final CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(applePayConnectionFactory)
                .build();

        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        final RestTemplate restTemplate = new RestTemplate(requestFactory);

        return restTemplate.postForObject(validateMerchantRequestData.getValidationURL(), validateMerchantData, Object.class);
    }

    /**
     * Places the apple pay order
     *
     * @param authorisationRequest the apple pay authorization request
     * @return the particular page for the scenario
     */
    @PostMapping(value = "/placeApplePayOrder")
    @RequireHardLogIn
    @ResponseBody
    public PlaceWalletOrderDataResponse placeApplePayOrder(@RequestBody final ApplePayAuthorisationRequest authorisationRequest) {

        handleAndSaveAddresses(authorisationRequest.getBillingContact());

        return placeWalletOrder(authorisationRequest.getToken().getPaymentData(), WalletPaymentType.APPLEPAY);
    }

    /**
     * Populate the address data based on the form values and set the billing address into the cart
     *
     * @param billingContact the billing contact from the form
     */
    protected void handleAndSaveAddresses(final ApplePayPaymentContact billingContact) {
        final AddressData addressData = checkoutComApplePayAddressReverseConverter.convert(billingContact);
        if (addressData != null) {
            final CustomerModel currentUserForCheckout = checkoutCustomerStrategy.getCurrentUserForCheckout();
            addressData.setEmail(currentUserForCheckout != null ? currentUserForCheckout.getContactEmail() : null);
        }

        getUserFacade().addAddress(addressData);

        checkoutComAddressFacade.setCartBillingDetails(addressData);
    }

    protected Optional<ApplePayValidateMerchantData> getValidateMerchantData() {
        final Optional<ApplePaySettingsData> applePayConfig = checkoutComMerchantConfigurationFacade.getApplePaySettings();
        return applePayConfig.map(applePaySettingsData -> checkoutComApplePayConfigDataToValidateMerchantRequestDTOPopulatingConverter.convert(applePaySettingsData));
    }
}