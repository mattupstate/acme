import { NgModule } from '@angular/core';

import { SettingsModule } from './settings/settings.module';

@NgModule({
  exports: [SettingsModule],
})
export class FeatureModule {}
