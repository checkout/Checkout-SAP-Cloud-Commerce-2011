import { NgModule } from '@angular/core';
import { checkoutTranslationChunksConfig, checkoutTranslations } from '@spartacus/checkout/assets';
import { CHECKOUT_FEATURE, CheckoutRootModule } from '@spartacus/checkout/root';
import { CmsConfig, I18nConfig, provideConfig } from '@spartacus/core';

@NgModule({
  declarations: [],
  imports: [
    CheckoutRootModule,
  ],
  providers: [provideConfig({
    featureModules: {
      [CHECKOUT_FEATURE]: {
        module: () =>
          import('@spartacus/checkout').then((m) => m.CheckoutModule),
      },
    },
  } as CmsConfig),
    provideConfig({
      i18n: {
        resources: checkoutTranslations,
        chunks: checkoutTranslationChunksConfig,
      },
    } as I18nConfig)
  ]
})
export class CheckoutFeatureModule {
}
