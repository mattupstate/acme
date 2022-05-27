import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatToolbarModule } from '@angular/material/toolbar';

@NgModule({
  exports: [
    MatIconModule,
    MatButtonModule,
    MatSidenavModule,
    MatSnackBarModule,
    MatListModule,
    MatSnackBarModule,
    MatMenuModule,
    MatToolbarModule,
  ],
})
export class AppMaterialModule {}
