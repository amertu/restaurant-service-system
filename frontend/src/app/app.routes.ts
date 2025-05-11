import { Routes } from '@angular/router';
import { HomeComponent } from './components/pages/home/home.component';
import { LoginComponent } from './components/pages/login/login.component';
import {NegateAuthGuard} from './guards/negate-auth.guard';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/pages/message/message.component';
import {ReservationFilterComponent} from './components/pages/reservation/reservation-filter/reservation-filter.component';
import {BillingComponent} from './components/pages/billing/billing.component';
import {DishComponent} from './components/pages/dish/dish.component';
import {UserComponent} from './components/pages/user/user.component';
import {FloorLayoutComponent} from './components/pages/floor-layout/floor-layout.component';
import {TableResolveConflictComponent} from './components/pages/floor-layout/table/table-resolve-conflict/table-resolve-conflict.component';
// add all your routes

export const appRoutes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', canActivate: [NegateAuthGuard], component: LoginComponent},
  {path: 'message', canActivate: [AuthGuard], component: MessageComponent},
  {path: 'reservation', canActivate: [AuthGuard], component: ReservationFilterComponent},
  {path: 'billing', canActivate: [AuthGuard], component: BillingComponent},
  {path: 'dish', canActivate: [AuthGuard], component: DishComponent},
  {path: 'users', canActivate: [AuthGuard], component: UserComponent},
  {path: 'floorplan', canActivate: [AuthGuard], component: FloorLayoutComponent},
  {path: 'table/:id/resolve-delete', canActivate: [AuthGuard], component: TableResolveConflictComponent},
  {path: 'table/:id/resolve-deactivate', canActivate: [AuthGuard], component: TableResolveConflictComponent}
];
