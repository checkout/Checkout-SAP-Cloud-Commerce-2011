package com.checkout.hybris.addon.controllers.payment;

import com.checkout.hybris.facades.address.CheckoutComAddressFacade;
import com.checkout.hybris.facades.beans.GooglePayAuthorisationRequest;
import com.checkout.hybris.facades.beans.GooglePayPaymentContact;
import com.checkout.hybris.facades.beans.PlaceWalletOrderDataResponse;
import com.checkout.hybris.facades.enums.WalletPaymentType;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/checkout/payment/checkout-com/googlepay")
public class CheckoutComGooglePayController extends CheckoutComAbstractWalletPaymentController {

    @Resource
    protected CheckoutComAddressFacade checkoutComAddressFacade;
    @Resource
    protected Converter<GooglePayPaymentContact, AddressData> checkoutComGooglePayAddressReverseConverter;
    @Resource
    protected CheckoutCustomerStrategy checkoutCustomerStrategy;

    /**
     * Places the google pay order
     *
     * @param authorisationRequest the google pay authorization request
     * @return the particular page for the scenario
     */
    @PostMapping(value = "/placeGooglePayOrder", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @RequireHardLogIn
    @ResponseBody
    public PlaceWalletOrderDataResponse authoriseOrder(@RequestBody final GooglePayAuthorisationRequest authorisationRequest) {

        handleAndSaveAddresses(authorisationRequest.getBillingAddress());

        return placeWalletOrder(authorisationRequest.getToken(), WalletPaymentType.GOOGLEPAY);
    }

    /**
     * Populate the address data based on the form values and set the billing address into the cart
     *
     * @param billingContact the billing contact from the form
     */
    protected void handleAndSaveAddresses(final GooglePayPaymentContact billingContact) {
        final AddressData addressData = checkoutComGooglePayAddressReverseConverter.convert(billingContact);
        if (addressData != null) {
            final CustomerModel currentUserForCheckout = checkoutCustomerStrategy.getCurrentUserForCheckout();
            addressData.setEmail(currentUserForCheckout != null ? currentUserForCheckout.getContactEmail() : null);
        }

        getUserFacade().addAddress(addressData);

        checkoutComAddressFacade.setCartBillingDetails(addressData);
    }
}
