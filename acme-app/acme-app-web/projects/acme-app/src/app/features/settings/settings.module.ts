import { NgModule } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { SettingsComponent } from './settings.component';

@NgModule({
  imports: [MatToolbarModule],
  declarations: [SettingsComponent],
})
export class SettingsModule {}
