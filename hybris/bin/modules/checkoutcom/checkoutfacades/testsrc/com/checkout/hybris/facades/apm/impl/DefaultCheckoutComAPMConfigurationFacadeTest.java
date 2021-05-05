package com.checkout.hybris.facades.apm.impl;

import com.checkout.hybris.core.apm.services.CheckoutComAPMConfigurationService;
import com.checkout.hybris.core.model.CheckoutComAPMConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.checkout.common.Currency.GBP;
import static java.util.Locale.UK;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCheckoutComAPMConfigurationFacadeTest {

    public static final String APM_CODE = "apmCode";
    @InjectMocks
    private DefaultCheckoutComAPMConfigurationFacade testObj;

    @Mock
    private CheckoutComAPMConfigurationService checkoutComAPMConfigurationServiceMock;
    @Mock
    private CheckoutComAPMConfigurationModel apmConfigurationMock;

    @Before
    public void setUp() {
        when(apmConfigurationMock.getCode()).thenReturn(APM_CODE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isAvailable_WhenCountryCodeIsNull_ShouldThrowException() {
        testObj.isAvailable(apmConfigurationMock, null, GBP);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isAvailable_WhenCurrencyCodeIsNull_ShouldThrowException() {
        testObj.isAvailable(apmConfigurationMock, UK.getCountry(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isAvailable_WhenApmConfigurationIsNull_ShouldThrowException() {
        testObj.isAvailable(null, UK.getCountry(), GBP);
    }

    @Test
    public void isAvailable_WhenApmIsAvailable_ShouldReturnTrue() {
        when(checkoutComAPMConfigurationServiceMock.isApmAvailable(apmConfigurationMock, UK.getCountry(), GBP)).thenReturn(true);

        assertTrue(testObj.isAvailable(apmConfigurationMock, UK.getCountry(), GBP));
    }

    @Test
    public void isAvailable_WhenApmIsNotAvailable_ShouldReturnFalse() {
        when(checkoutComAPMConfigurationServiceMock.isApmAvailable(apmConfigurationMock, UK.getCountry(), GBP)).thenReturn(false);

        assertFalse(testObj.isAvailable(apmConfigurationMock, UK.getCountry(), GBP));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isRedirect_WhenApmConfigurationIsNull_ShouldThrowException() {
        testObj.isRedirect(null);
    }

    @Test
    public void isRedirect_WhenApmDefined_ShouldReturnConfiguredValue() {
        when(checkoutComAPMConfigurationServiceMock.isApmRedirect(APM_CODE)).thenReturn(true);

        assertTrue(testObj.isRedirect(apmConfigurationMock));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isUserDataRequiredRedirect_WhenApmConfigurationIsNull_ShouldThrowException() {
        testObj.isUserDataRequiredRedirect(null);
    }

    @Test
    public void isUserDataRequiredRedirect_WhenApmDefined_ShouldReturnConfiguredValue() {
        when(checkoutComAPMConfigurationServiceMock.isApmUserDataRequired(APM_CODE)).thenReturn(true);

        assertTrue(testObj.isUserDataRequiredRedirect(apmConfigurationMock));
    }
}
