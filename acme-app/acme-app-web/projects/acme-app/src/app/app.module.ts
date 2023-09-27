import { HttpClientModule } from '@angular/common/http';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { EffectsModule } from '@ngrx/effects';
import { StoreRouterConnectingModule } from '@ngrx/router-store';
import { Store, StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { filter } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { AppContainerComponent } from './app-container.component';
import { AppMaterialModule } from './app-material.module';
import { IsAuthenticated, IsUnauthenticated } from './app-route-guard.service';
import { AppRoutingModule } from './app-routing.module';
import { appStartup } from './app.actions';
import { AppComponent } from './app.component';
import { AppEffects, StartupAffects } from './app.effects';
import { GlobalErrorsService } from './app.global-errors.service';
import { selectStartupState } from './app.selectors';
import { reducers, StartupState } from './app.state';
import { DeviceAwarenessService } from './device-awareness.service';
import { FeatureModule } from './features';
import { IdentityModule } from './features/identity/identity.module';
import { NotificationsAlertModule } from './features/notifications/notifications-alert.module';

export const initializer = (store: Store) => () =>
  new Promise<any>((resolve) => {
    store
      .select(selectStartupState)
      .pipe(
        filter((state) => {
          return state === StartupState.COMPLETE;
        })
      )
      .subscribe(resolve);
    store.dispatch(appStartup());
  });

@NgModule({
  declarations: [AppComponent, AppContainerComponent],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    AppMaterialModule,
    StoreModule.forRoot(reducers),
    EffectsModule.forRoot([StartupAffects, AppEffects]),
    StoreRouterConnectingModule.forRoot(),
    StoreDevtoolsModule.instrument({
      maxAge: 25,
      logOnly: environment.production,
      autoPause: true,
    }),
    NotificationsAlertModule,
    IdentityModule.forRoot(environment.identity.kratosUrl),
    FeatureModule,
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      multi: true,
      deps: [Store],
    },
    // {
    //   provide: HTTP_INTERCEPTORS,
    //   useClass: IdentityTokenHttpInterceptor,
    //   multi: true,
    // },
    {
      provide: Window,
      useValue: window,
    },
    GlobalErrorsService,
    DeviceAwarenessService,
    IsAuthenticated,
    IsUnauthenticated,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
