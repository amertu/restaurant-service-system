import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {AlertComponent} from './components/alert/alert.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {PasswordResetComponent} from './components/pages/user/password-reset/password-reset.component';
import {HomeComponent} from './components/pages/home/home.component';
import {LoginComponent} from './components/pages/login/login.component';
import {MessageComponent} from './components/pages/message/message.component';
import {ReservationComponent} from './components/pages/reservation/reservation.component';
import {ReservationFormComponent} from './components/pages/reservation/reservation-form/reservation-form.component';
import {ReservationAddComponent} from './components/pages/reservation/reservation-add/reservation-add.component';
import {ReservationEditComponent} from './components/pages/reservation/reservation-edit/reservation-edit.component';
import {ReservationDeleteComponent} from './components/pages/reservation/reservation-delete/reservation-delete.component';
import {BillingComponent} from './components/pages/billing/billing.component';
import {BillingAddComponent} from './components/pages/billing/billing-add/billing-add.component';
import {DishComponent} from './components/pages/dish/dish.component';
import {DishAddComponent} from './components/pages/dish/dish-add/dish-add.component';
import {DishEditComponent} from './components/pages/dish/dish-edit/dish-edit.component';
import {DishDeleteComponent} from './components/pages/dish/dish-delete/dish-delete.component';
import {TableComponent} from './components/pages/floor-layout/table/table.component';
import {TableAddComponent} from './components/pages/floor-layout/table/table-add/table-add.component';
import {TableEditComponent} from './components/pages/floor-layout/table/table-edit/table-edit.component';
import {TableDeleteComponent} from './components/pages/floor-layout/table/table-delete/table-delete.component';
import {UserComponent} from './components/pages/user/user.component';
import {UserAddComponent} from './components/pages/user/user-add/user-add.component';
import {UserEditComponent} from './components/pages/user/user-edit/user-edit.component';
import {UserDeleteComponent} from './components/pages/user/user-delete/user-delete.component';
import {DateSelectorComponent} from './components/date-selector/date-selector.component';
import {DurationSelectorComponent} from './components/duration-selector/duration-selector.component';
import {TimeSelectorComponent} from './components/time-selector/time-selector.component';
import {FloorLayoutComponent} from './components/pages/floor-layout/floor-layout.component';
import { ReservationDetailsComponent } from './components/pages/reservation/reservation-details/reservation-details.component';
import { ReservationFilterComponent } from './components/pages/reservation/reservation-filter/reservation-filter.component';
import { TableResolveConflictComponent } from './components/pages/floor-layout/table/table-resolve-conflict/table-resolve-conflict.component';
import { TableDeactivateComponent } from './components/pages/floor-layout/table/table-deactivate/table-deactivate.component';
import { AlertContentForNgbModalComponent } from './components/alert/alert-content-for-ngb-modal/alert-content-for-ngb-modal.component';
import { ReservationFinishComponent } from './components/pages/reservation/reservation-finish/reservation-finish.component';

@NgModule({
  declarations: [
    AppComponent,
    AlertComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    ReservationComponent,
    ReservationFormComponent,
    ReservationAddComponent,
    ReservationEditComponent,
    ReservationDeleteComponent,
    BillingComponent,
    BillingAddComponent,
    DishComponent,
    DishAddComponent,
    DishEditComponent,
    DishDeleteComponent,
    TableComponent,
    TableAddComponent,
    TableEditComponent,
    TableDeleteComponent,
    UserComponent,
    UserAddComponent,
    UserEditComponent,
    UserDeleteComponent,
    DateSelectorComponent,
    DurationSelectorComponent,
    TimeSelectorComponent,
    FloorLayoutComponent,
    PasswordResetComponent,
    ReservationDetailsComponent,
    ReservationFilterComponent,
    TableResolveConflictComponent,
    TableDeactivateComponent,
    AlertContentForNgbModalComponent,
    ReservationFinishComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent],
  entryComponents: [
    ReservationAddComponent,
    ReservationEditComponent
  ]
})
export class AppModule {
}
