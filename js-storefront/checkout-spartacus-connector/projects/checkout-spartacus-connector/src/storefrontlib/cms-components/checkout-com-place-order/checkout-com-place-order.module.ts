import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CheckoutComPlaceOrderComponent } from './checkout-com-place-order.component';
import { ConfigModule, UrlModule, I18nModule, CmsConfig } from '@spartacus/core';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [CheckoutComPlaceOrderComponent],
  imports: [
    CommonModule,
    RouterModule,
    UrlModule,
    I18nModule,
    ReactiveFormsModule,
    ConfigModule.withConfig({
      cmsComponents: {
        CheckoutPlaceOrder: {
          component: CheckoutComPlaceOrderComponent
        }
      }
    } as CmsConfig)
  ]
})
export class CheckoutComPlaceOrderModule {}
