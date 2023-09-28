import { Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.less'],
})
export class SignInComponent {
  hidePassword = true;
  
  signInForm = this.formBuilder.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  @Input() set enabled(value: boolean) {
    value ? this.signInForm.enable() : this.signInForm.disable();
  }

  @Input() postVerify: boolean = false;
  @Input() errorCode: number | null = null;
  @Input() recoverLink = '/recover';
  @Input() registerLink = '/register';
  @Input() verifyLink = "/verify";

  @Output() formSubmit = new EventEmitter<{
    email: string;
    password: string;
  }>();

  @Output() openIdRequest = new EventEmitter<{
    provider: string;
  }>();

  constructor(private formBuilder: UntypedFormBuilder) {}

  hasError(control: string, validator: string) {
    return this.signInForm.controls[control].hasError(validator);
  }

  onOpenIdRequest(provider: string) {
    this.openIdRequest.emit({provider})
  }

  onSubmit(data: { email: string; password: string }) {
    if (this.signInForm.valid) {
      this.formSubmit.emit(data);
    }
  }
}
