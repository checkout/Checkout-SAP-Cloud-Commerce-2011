import { Pipe, PipeTransform } from '@angular/core';
import { waitForAsync, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import {
  CheckoutService,
  DaysOfWeek,
  I18nTestingModule,
  ORDER_TYPE,
  recurrencePeriod,
  RoutingService,
  ScheduleReplenishmentForm,
} from '@spartacus/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { CheckoutComPlaceOrderComponent } from './checkout-com-place-order.component';
import { CheckoutReplenishmentFormService, LaunchDialogService, LAUNCH_CALLER, CheckoutStepService } from '@spartacus/storefront';
import { CheckoutComCheckoutService } from '../../../core/services/checkout-com-checkout.service';

const mockReplenishmentOrderFormData: ScheduleReplenishmentForm = {
  numberOfDays: 'test-number-days',
  nthDayOfMonth: 'test-day-month',
  recurrencePeriod: recurrencePeriod.WEEKLY,
  numberOfWeeks: 'test-num-of-weeks',
  replenishmentStartDate: 'test-date',
  daysOfWeek: [DaysOfWeek.FRIDAY],
};

const mockReplenishmentOrderFormData$ = new BehaviorSubject<ScheduleReplenishmentForm>(
  mockReplenishmentOrderFormData
);

let clearedPlaceOrderStatus = new BehaviorSubject<boolean>(false);

class MockCheckoutService {
  placeOrder(): void {}

  scheduleReplenishmentOrder(
    _scheduleReplenishmentForm: ScheduleReplenishmentForm,
    _termsChecked: boolean
  ): void {}

  getPlaceOrderLoading(): Observable<boolean> {
    return of();
  }

  getPlaceOrderSuccess(): Observable<boolean> {
    return of();
  }

  getPlaceOrderError(): Observable<boolean> {
    return of();
  }

  getCurrentOrderType(): Observable<ORDER_TYPE> {
    return of();
  }

  clearPlaceOrderState(): void {clearedPlaceOrderStatus.next(true)}

  getOrderResultFromState() {
    return of({});
  }
}

class MockCheckoutReplenishmentFormService {
  getScheduleReplenishmentFormData(): Observable<ScheduleReplenishmentForm> {
    return mockReplenishmentOrderFormData$.asObservable();
  }

  setScheduleReplenishmentFormData(
    _formData: ScheduleReplenishmentForm
  ): void {}

  resetScheduleReplenishmentFormData(): void {}
}

class MockRoutingService {
  go(): void {}

  getRouterState() {
    return of({});
  }
}

class MockCheckoutStepService {
  steps = {};
}

class MockLaunchDialogService {
  launch() {}
  clear() {}
}

@Pipe({
  name: 'cxUrl',
})
class MockUrlPipe implements PipeTransform {
  transform(): any {}
}

describe('CheckoutComPlaceOrderComponent', () => {
  let component: CheckoutComPlaceOrderComponent;
  let fixture: ComponentFixture<CheckoutComPlaceOrderComponent>;
  let controls: FormGroup['controls'];

  let checkoutService: CheckoutService;
  let checkoutComCheckoutService: CheckoutComCheckoutService;
  let checkoutReplenishmentFormService: CheckoutReplenishmentFormService;
  let routingService: RoutingService;
  let stepsService: CheckoutStepService;
  let launchDialogService: LaunchDialogService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, RouterTestingModule, I18nTestingModule],
        declarations: [MockUrlPipe, CheckoutComPlaceOrderComponent],
        providers: [
          { provide: CheckoutService, useClass: MockCheckoutService },
          { provide: CheckoutComCheckoutService, useClass: MockCheckoutService },
          {
            provide: CheckoutReplenishmentFormService,
            useClass: MockCheckoutReplenishmentFormService,
          },
          { provide: RoutingService, useClass: MockRoutingService },
          { provide: CheckoutStepService, useClass: MockCheckoutStepService },
          { provide: LaunchDialogService, useClass: MockLaunchDialogService },
        ],
      }).compileComponents();

      clearedPlaceOrderStatus = new BehaviorSubject<boolean>(false);
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckoutComPlaceOrderComponent);
    component = fixture.componentInstance;

    controls = component.checkoutSubmitForm.controls;

    checkoutService = TestBed.inject(CheckoutService);
    checkoutComCheckoutService = TestBed.inject(CheckoutComCheckoutService);
    checkoutReplenishmentFormService = TestBed.inject(
      CheckoutReplenishmentFormService
    );
    routingService = TestBed.inject(RoutingService);
    stepsService = TestBed.inject(CheckoutStepService);
    launchDialogService = TestBed.inject(LaunchDialogService);

    spyOn(checkoutComCheckoutService, 'placeOrder').and.callThrough();
    spyOn(checkoutService, 'scheduleReplenishmentOrder').and.callThrough();
    spyOn(
      checkoutReplenishmentFormService,
      'setScheduleReplenishmentFormData'
    ).and.callThrough();
    spyOn(
      checkoutReplenishmentFormService,
      'resetScheduleReplenishmentFormData'
    ).and.callThrough();

  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should clear place order status', () => {
    expect(clearedPlaceOrderStatus.getValue()).toBeTrue();
  });

  describe('when order type is PLACE_ORDER', () => {
    it('should not place order when checkbox not checked', () => {
      submitForm(ORDER_TYPE.PLACE_ORDER, false);

      expect(checkoutComCheckoutService.placeOrder).not.toHaveBeenCalled();
      expect(checkoutService.scheduleReplenishmentOrder).not.toHaveBeenCalled();
    });

    it('should place order when checkbox checked', () => {
      submitForm(ORDER_TYPE.PLACE_ORDER, true);

      expect(checkoutComCheckoutService.placeOrder).toHaveBeenCalled();
      expect(checkoutService.scheduleReplenishmentOrder).not.toHaveBeenCalled();
    });

    it('should NOT change page and reset form data when there is no successful place order', () => {
      spyOn(routingService, 'go').and.stub();

      component.currentOrderType = ORDER_TYPE.PLACE_ORDER;
      component.onSuccess(false);

      expect(routingService.go).not.toHaveBeenCalled();
    });

    it('should change page and reset form data on a successful place order', () => {
      spyOn(routingService, 'go').and.stub();

      component.currentOrderType = ORDER_TYPE.PLACE_ORDER;
      component.onSuccess(true);

      expect(routingService.go).toHaveBeenCalledWith({
        cxRoute: 'orderConfirmation',
      });
    });
  });

  describe('when order type is SCHEDULE_REPLENISHMENT_ORDER', () => {
    it('should not schedule a replenishment order when checkbox not checked', () => {
      submitForm(ORDER_TYPE.SCHEDULE_REPLENISHMENT_ORDER, false);

      expect(checkoutComCheckoutService.placeOrder).not.toHaveBeenCalled();
      expect(checkoutService.scheduleReplenishmentOrder).not.toHaveBeenCalled();
    });

    it('should NOT change page and reset form data when there is no successful replenishment order', () => {
      spyOn(routingService, 'go').and.stub();

      component.currentOrderType = ORDER_TYPE.SCHEDULE_REPLENISHMENT_ORDER;
      component.onSuccess(false);

      expect(routingService.go).not.toHaveBeenCalled();
      expect(
        checkoutReplenishmentFormService.resetScheduleReplenishmentFormData
      ).not.toHaveBeenCalled();
    });

    it('should change page and reset form data on a successful replenishment order', () => {
      spyOn(routingService, 'go').and.stub();

      component.currentOrderType = ORDER_TYPE.SCHEDULE_REPLENISHMENT_ORDER;
      component.onSuccess(true);

      expect(routingService.go).toHaveBeenCalledWith({
        cxRoute: 'replenishmentConfirmation',
      });
      expect(
        checkoutReplenishmentFormService.resetScheduleReplenishmentFormData
      ).toHaveBeenCalled();
    });
  });

  describe('Place order UI', () => {
    beforeEach(() => {
      component.ngOnInit();
      controls.termsAndConditions.setValue(true);
    });

    it('should have button DISABLED when a checkbox for weekday in WEEKLY view is NOT checked and terms and condition checked', () => {
      mockReplenishmentOrderFormData$.next({
        ...mockReplenishmentOrderFormData,
        daysOfWeek: [],
      });

      fixture.detectChanges();

      expect(
        fixture.debugElement.nativeElement.querySelector('.btn-primary')
          .disabled
      ).toEqual(true);
    });
  });

  function submitForm(orderType: ORDER_TYPE, isTermsCondition: boolean): void {
    component.currentOrderType = orderType;
    controls.termsAndConditions.setValue(isTermsCondition);
    component.submitForm();
  }
});
