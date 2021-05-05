package com.checkout.hybris.core.apm.services.impl;

import com.checkout.hybris.core.apm.configuration.CheckoutComAPMConfigurationSettings;
import com.checkout.hybris.core.model.CheckoutComAPMConfigurationModel;
import com.checkout.hybris.core.model.CheckoutComFawryConfigurationModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;
import java.util.Optional;

import static com.checkout.common.Currency.EUR;
import static com.checkout.common.Currency.GBP;
import static com.checkout.hybris.core.payment.enums.CheckoutComPaymentType.FAWRY;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Locale.FRANCE;
import static java.util.Locale.UK;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCheckoutComAPMConfigurationServiceTest {

    private static final String APM_CODE = "apmCode";

    @InjectMocks
    private DefaultCheckoutComAPMConfigurationService testObj;

    @Mock
    private CheckoutComAPMConfigurationModel apmConfigurationMock;
    @Mock
    private CountryModel restrictedCountryMock;
    @Mock
    private CurrencyModel restrictedCurrencyMock;
    @Mock
    private GenericDao<CheckoutComAPMConfigurationModel> checkoutComApmConfigurationDaoMock;
    @Mock
    private CheckoutComFawryConfigurationModel fawryConfigurationModelMock;
    @Mock
    private Map<String, CheckoutComAPMConfigurationSettings> checkoutComAPMConfigurationSettingsMock;
    @Mock
    private CheckoutComAPMConfigurationSettings apmConfigurationSettingsMock;

    @Before
    public void setUp() {
        when(restrictedCountryMock.getIsocode()).thenReturn(FRANCE.getCountry());
        when(restrictedCurrencyMock.getIsocode()).thenReturn(EUR);
        when(apmConfigurationMock.getRestrictedCountries()).thenReturn(ImmutableSet.of(restrictedCountryMock));
        when(apmConfigurationMock.getRestrictedCurrencies()).thenReturn(ImmutableSet.of(restrictedCurrencyMock));
        when(checkoutComAPMConfigurationSettingsMock.containsKey(APM_CODE)).thenReturn(Boolean.TRUE);
        when(checkoutComAPMConfigurationSettingsMock.get(APM_CODE)).thenReturn(apmConfigurationSettingsMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isApmAvailable_WhenCountryCodeIsEmpty_ShouldThrowException() {
        testObj.isApmAvailable(apmConfigurationMock, UK.getCountry(), "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void isApmAvailable_WhenCurrencyCodeIsEmpty_ShouldThrowException() {
        testObj.isApmAvailable(apmConfigurationMock, "", GBP);
    }

    @Test
    public void isApmAvailable_WhenApmConfigurationIsNull_ShouldReturnTrue() {
        assertTrue(testObj.isApmAvailable(null, UK.getCountry(), GBP));
    }

    @Test
    public void isApmAvailable_WhenApmConfigurationDoesNotHaveRestrictions_ShouldReturnTrue() {
        when(apmConfigurationMock.getRestrictedCountries()).thenReturn(emptySet());
        when(apmConfigurationMock.getRestrictedCurrencies()).thenReturn(emptySet());

        assertTrue(testObj.isApmAvailable(apmConfigurationMock, UK.getCountry(), GBP));
    }

    @Test
    public void isApmAvailable_WhenApmRestrictedForDifferentCountry_ShouldReturnFalse() {
        when(apmConfigurationMock.getRestrictedCurrencies()).thenReturn(emptySet());

        assertFalse(testObj.isApmAvailable(apmConfigurationMock, UK.getCountry(), EUR));
    }

    @Test
    public void isApmAvailable_WhenApmRestrictedForDifferentCurrency_ShouldReturnFalse() {
        when(apmConfigurationMock.getRestrictedCountries()).thenReturn(emptySet());

        assertFalse(testObj.isApmAvailable(apmConfigurationMock, UK.getCountry(), GBP));
    }

    @Test
    public void isApmAvailable_WhenApmRestrictedForDifferentCountryAndCurrency_ShouldReturnTrue() {
        assertTrue(testObj.isApmAvailable(apmConfigurationMock, FRANCE.getCountry(), EUR));
    }

    @Test
    public void getApmConfigurationByCode_WhenConfigurationIsFound_ShouldReturnIt() {
        when(checkoutComApmConfigurationDaoMock.find(ImmutableMap.of(CheckoutComAPMConfigurationModel.CODE, FAWRY.name()))).thenReturn(asList(fawryConfigurationModelMock));

        Optional<CheckoutComAPMConfigurationModel> result = testObj.getApmConfigurationByCode(FAWRY.name());

        assertTrue(result.isPresent());
        assertEquals(fawryConfigurationModelMock, result.get());
    }

    @Test
    public void getApmConfigurationByCode_WhenConfigurationIsNotFound_ShouldReturnOptionalEmpty() {
        when(checkoutComApmConfigurationDaoMock.find(ImmutableMap.of(CheckoutComAPMConfigurationModel.CODE, FAWRY.name()))).thenReturn(emptyList());

        Optional<CheckoutComAPMConfigurationModel> result = testObj.getApmConfigurationByCode(FAWRY.name());

        assertFalse(result.isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getApmConfigurationByCode_WhenConfigurationCodeIsNull_ShouldThrowException() {
        Optional<CheckoutComAPMConfigurationModel> result = testObj.getApmConfigurationByCode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isApmRedirect_WhenApmCodeEmpty_ShouldThrowException() {
        testObj.isApmRedirect("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void isApmRedirect_WhenApmCodeMissing_ShouldThrowException() {
        when(checkoutComAPMConfigurationSettingsMock.containsKey(APM_CODE)).thenReturn(false);

        testObj.isApmRedirect(APM_CODE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isApmRedirect_WhenApmCodeNotConfigured_ShouldThrowException() {
        when(checkoutComAPMConfigurationSettingsMock.containsKey(APM_CODE)).thenReturn(true);
        when(checkoutComAPMConfigurationSettingsMock.get(APM_CODE)).thenReturn(null);

        testObj.isApmRedirect(APM_CODE);
    }

    @Test
    public void isApmRedirect_WhenApmCodetConfigured_ShouldReturnConfigurationValue() {
        when(apmConfigurationSettingsMock.getIsApmRedirect()).thenReturn(Boolean.TRUE);

        assertTrue(testObj.isApmRedirect(APM_CODE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isApmUserDataRequired_WhenApmCodeEmpty_ShouldThrowException() {
        testObj.isApmUserDataRequired("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void isApmUserDataRequired_WhenApmCodeMissing_ShouldThrowException() {
        when(checkoutComAPMConfigurationSettingsMock.containsKey(APM_CODE)).thenReturn(false);

        testObj.isApmUserDataRequired(APM_CODE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isApmUserDataRequired_WhenApmCodeNotConfigured_ShouldThrowException() {
        when(checkoutComAPMConfigurationSettingsMock.containsKey(APM_CODE)).thenReturn(true);
        when(checkoutComAPMConfigurationSettingsMock.get(APM_CODE)).thenReturn(null);

        testObj.isApmUserDataRequired(APM_CODE);
    }

    @Test
    public void isApmUserDataRequired_WhenApmCodetConfigured_ShouldReturnConfigurationValue() {
        when(apmConfigurationSettingsMock.getIsApmUserDataRequired()).thenReturn(Boolean.TRUE);

        assertTrue(testObj.isApmUserDataRequired(APM_CODE));
    }
}
