import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatToolbarModule } from '@angular/material/toolbar';
import {
  HttpPracticesCollectionService,
  PracticesCollectionService,
} from './practices-collection.service';
import { PracticesListItemComponent } from './practices-list-item/practices-list-item.component';
import { PracticesListComponent } from './practices-list.component';
import { PracticesRoutingModule } from './practices-routing.module';
import { PracticesViewComponent } from './practices-view.component';
import { PracticesComponent } from './practices.component';

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatToolbarModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    PracticesRoutingModule,
  ],
  declarations: [
    PracticesComponent,
    PracticesListComponent,
    PracticesViewComponent,
    PracticesListItemComponent,
  ],
  providers: [
    {
      provide: PracticesCollectionService,
      useClass: HttpPracticesCollectionService,
    },
    // {
    //   provide: HTTP_INTERCEPTORS,
    //   useClass: IdentityTokenHttpInterceptor,
    //   multi: true,
    // },
  ],
})
export class PracticesModule {}
