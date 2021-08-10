import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CheckoutComOrderOverviewComponent } from './checkout-com-order-overview.component';
import { CardModule } from '@spartacus/storefront';
import { I18nModule } from '@spartacus/core';
import { CheckoutComOrderReviewComponent } from '../checkout-com-order-review/checkout-com-order-review.component';


@NgModule({
  declarations: [CheckoutComOrderOverviewComponent],
  imports: [
    CommonModule,
    I18nModule,
    CardModule
  ],
  exports: [
    CheckoutComOrderOverviewComponent
  ],
})
export class CheckoutComOrderOverviewModule {
}
