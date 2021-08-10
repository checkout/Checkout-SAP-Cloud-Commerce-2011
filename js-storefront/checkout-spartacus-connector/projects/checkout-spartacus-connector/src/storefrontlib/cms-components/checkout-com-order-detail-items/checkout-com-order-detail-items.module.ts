import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CheckoutComOrderDetailItemsComponent } from './checkout-com-order-detail-items.component';
import { NgxQRCodeModule } from '@techiediaries/ngx-qrcode';
import {
  CardModule,
  CartSharedModule, OrderDetailsModule,
  OrderOverviewModule,
  PromotionsModule,
  SpinnerModule
} from '@spartacus/storefront';
import { FeaturesConfigModule, I18nModule, UrlModule } from '@spartacus/core';

@NgModule({
  declarations: [CheckoutComOrderDetailItemsComponent],
  imports: [
    CartSharedModule,
    CardModule,
    CommonModule,
    I18nModule,
    FeaturesConfigModule,
    PromotionsModule,
    OrderOverviewModule,
    UrlModule,
    SpinnerModule,
    NgxQRCodeModule,
    OrderDetailsModule
  ]
})
export class CheckoutComOrderDetailItemsModule { }
