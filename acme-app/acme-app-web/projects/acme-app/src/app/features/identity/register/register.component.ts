import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.less'],
})
export class RegisterComponent implements OnInit {
  hidePassword = true;
  requested = false;

  registerForm: FormGroup = this.formBuilder.group({
    root: [],
    email: ['', Validators.required],
    password: ['', Validators.required],
    givenName: ['', Validators.required],
    familyName: ['', Validators.required],
  });

  @Input() set enabled(value: boolean) {
    value ? this.registerForm.enable() : this.registerForm.disable();
  }

  @Input() set asyncErrors(value: {
    errors: Array<string>,
    attributes: {
      email: Array<string>,
      password: Array<string>,
      givenName: Array<string>,
      familyName: Array<string>
    }
  } | null) {
    if (value) {
      if (value.errors.length > 0) {
        this.root.setErrors({
          async: value.errors[0]
        })
      }
      if (value.attributes.password.length > 0) {
        this.password.setErrors({
          async: value.attributes.password[0]
        })
      }
    } else {
      this.root.setErrors({})
      this.email.setErrors({})
      this.password.setErrors({})
      this.givenName.setErrors({})
      this.familyName.setErrors({})
    }
  }

  @Input() signInLink = '/sign-in';


  @Input() set toggleView(value: boolean | null) {
    this.requested = value || false;
  }

  @Output() formSubmit = new EventEmitter<{
    email: string;
    password: string;
    givenName: string;
    familyName: string;
  }>();

  @Output() openIdRequest = new EventEmitter<{
    provider: string;
  }>();

  get root() { return this.registerForm.get('root')! }
  get email() { return this.registerForm.get('email')! }
  get password() { return this.registerForm.get('password')! }
  get givenName() { return this.registerForm.get('givenName')! }
  get familyName() { return this.registerForm.get('familyName')! }

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.reset();
  }

  reset() {
    this.requested = false;
  }

  displaySafeError(field: string, error: string): string {
    const text = this.registerForm.controls[field]?.errors![error];
    return text.split(" ").slice(0, 10).join(" ")
  }

  displayExtendedError(field: string, error: string) {

  }

  hasError(control: string, validator: string): boolean {
    return this.registerForm.controls[control].hasError(validator);
  }

  onOpenIdRequest(provider: string) {
    this.openIdRequest.emit({provider})
  }

  onSubmit(data: {
    email: string;
    password: string;
    givenName: string;
    familyName: string;
  }) {
    if (this.registerForm.valid) {
      this.formSubmit.emit(data);
    }
  }
}
