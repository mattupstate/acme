import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable()
export class GlobalErrorsService {
  constructor(private window: Window, private snackBar: MatSnackBar) {}

  private getMessage(id: string): string {
    return this.window.document.getElementById(id)!.innerText;
  }

  private getDismissMessage(): string {
    return this.getMessage('global-error-dismiss-text');
  }

  private showSnackBar(messageId: string) {
    this.snackBar.open(
      this.getMessage(`global-error-${messageId}`),
      this.getDismissMessage(),
      {
        duration: 10000,
        verticalPosition: 'bottom',
      }
    );
  }

  dismiss() {
    this.snackBar.dismiss();
  }

  unauthenticatedAccess() {
    this.showSnackBar('unauthenticated-access');
  }
}
