import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CheckoutComOrderConfirmationThankYouMessageComponent } from './checkout-com-order-confirmation-thank-you-message.component';
import { OrderConfirmationModule, PwaModule, SpinnerModule } from '@spartacus/storefront';
import { I18nModule } from '@spartacus/core';
import { NgxQRCodeModule } from '@techiediaries/ngx-qrcode';

@NgModule({
  declarations: [CheckoutComOrderConfirmationThankYouMessageComponent],
  imports: [
    CommonModule,
    OrderConfirmationModule,
    I18nModule,
    PwaModule,
    NgxQRCodeModule,
    SpinnerModule,
  ]
})
export class CheckoutComOrderConfirmationThankYouMessageModule { }
