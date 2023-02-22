import { Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-recover',
  templateUrl: './recover.component.html',
  styleUrls: ['./recover.component.less'],
})
export class RecoverComponent {
  recoverForm = this.formBuilder.group({
    email: ['', Validators.required],
  });

  recoveryCodeForm = this.formBuilder.group({
    recoveryCode: ['', Validators.required],
  });

  @Input() set enabled(value: boolean) {
    value ? this.recoverForm.enable() : this.recoverForm.disable();
  }

  @Input() set toggleView(value: boolean | null) {
    this.requested = value || false;
  }

  @Input() errorCode: number | null = null;
  @Input() signInLink = '/sign-in';

  @Output() formSubmit = new EventEmitter<{ email: string }>();
  @Output() recoverCodeSubmit = new EventEmitter<{
    email: string;
    code: string;
  }>();
  @Output() formReset = new EventEmitter();

  requested: boolean = false;
  forEmail: string = '';

  constructor(private formBuilder: UntypedFormBuilder) {}

  reset(event?: MouseEvent) {
    event?.preventDefault();
    this.requested = false;
    this.recoverForm.reset();
    this.formReset.emit();
  }

  onSubmit(data: { email: string }) {
    this.forEmail = data.email;
    this.formSubmit.emit(data);
  }

  onCodeSubmit(data: { code: string }) {
    this.recoverCodeSubmit.emit({ code: data.code, email: this.forEmail });
  }
}
