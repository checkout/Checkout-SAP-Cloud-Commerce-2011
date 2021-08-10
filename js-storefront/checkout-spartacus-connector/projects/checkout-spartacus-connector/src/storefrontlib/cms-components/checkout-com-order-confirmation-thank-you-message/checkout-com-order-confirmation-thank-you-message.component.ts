import { ChangeDetectionStrategy, Component } from '@angular/core';
import { OrderConfirmationThankYouMessageComponent } from '@spartacus/storefront';
import { NgxQrcodeElementTypes, NgxQrcodeErrorCorrectionLevels } from '@techiediaries/ngx-qrcode';

@Component({
  selector: 'lib-checkout-com-order-confirmation-thank-you-message',
  templateUrl: './checkout-com-order-confirmation-thank-you-message.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CheckoutComOrderConfirmationThankYouMessageComponent extends OrderConfirmationThankYouMessageComponent {
  elementType = NgxQrcodeElementTypes.IMG;
  correctionLevel = NgxQrcodeErrorCorrectionLevels.HIGH;

}
