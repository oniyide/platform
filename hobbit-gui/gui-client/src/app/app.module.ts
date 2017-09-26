import { EditComponent } from './challenges/edit/edit.component';
import { CustomHttp } from './custom-http.service';
import { BackendService } from './backend.service';
import { KeycloakService } from './auth/keycloak.service';
import { AuthGuardService } from './auth/auth-guard.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Http, HttpModule, XHRBackend, RequestOptions } from '@angular/http';
import { SlimLoadingBarModule, SlimLoadingBarService } from 'ng2-slim-loading-bar';
import { ModalModule } from 'ngx-bootstrap/modal';
import { DataTableModule, CalendarModule } from 'primeng/primeng';
import { AppComponent } from './app.component';
import { NavbarComponent } from './navbar/navbar.component';
import { HomeComponent } from './home/home.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { BenchmarkComponent } from './benchmark/benchmark.component';
import { BenchmarkComponent as UploadBenchmarkComponent } from './upload/benchmark/benchmark.component';
import { PageHeaderComponent } from './page-header/page-header.component';
import { SystemComponent } from './upload/system/system.component';
import { ConfigComponent } from './benchmark/config/config.component';
import { DynFormComponent } from './dyn-form/dyn-form.component';
import { ChallengesComponent } from './challenges/challenges.component';
import { EditComponent as ChallengesEditComponent } from './challenges/edit/edit.component';


const appRoutes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuardService] },
  { path: 'upload/benchmarks', component: UploadBenchmarkComponent, canActivate: [AuthGuardService] },
  { path: 'upload/systems', component: SystemComponent, canActivate: [AuthGuardService] },
  { path: 'benchmarks', component: BenchmarkComponent, canActivate: [AuthGuardService] },
  { path: 'challenges', component: ChallengesComponent, canActivate: [AuthGuardService] },
  { path: 'challenges/:id', component: ChallengesEditComponent, canActivate: [AuthGuardService] },
  { path: '**', component: NotFoundComponent }
];


export const httpProvide = {
  provide: Http,
  useFactory: httpClientFactory,
  deps: [XHRBackend, RequestOptions, KeycloakService, SlimLoadingBarService]
};
export function httpClientFactory(backend: XHRBackend, defaultOptions: RequestOptions, keycloakService: KeycloakService, slimLoadingBarService: SlimLoadingBarService): Http {
  return new CustomHttp(backend, defaultOptions, keycloakService, slimLoadingBarService);
}


@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    HomeComponent,
    NotFoundComponent,
    BenchmarkComponent,
    UploadBenchmarkComponent,
    PageHeaderComponent,
    SystemComponent,
    ConfigComponent,
    DynFormComponent,
    ChallengesComponent,
    ChallengesEditComponent,
  ],
  imports: [
    BrowserAnimationsModule,
    RouterModule.forRoot(appRoutes),
    BrowserModule,
    HttpModule,
    FormsModule,
    SlimLoadingBarModule.forRoot(),
    ModalModule.forRoot(),
    ReactiveFormsModule,
    DataTableModule,
    CalendarModule
  ],
  providers: [
    AuthGuardService,
    KeycloakService,
    BackendService,
    httpProvide
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
