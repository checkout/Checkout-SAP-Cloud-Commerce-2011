import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CheckoutComOrderDetailShippingComponent } from './checkout-com-order-detail-shipping.component';
import { CheckoutComOrderOverviewModule } from '../checkout-com-order-overview/checkout-com-order-overview.module';
import { CmsConfig, provideConfig } from '@spartacus/core';

@NgModule({
  declarations: [CheckoutComOrderDetailShippingComponent],
  imports: [
    CommonModule,
    CheckoutComOrderOverviewModule
  ],
  exports: [CheckoutComOrderDetailShippingComponent],
})
export class CheckoutComOrderDetailShippingModule {
}
