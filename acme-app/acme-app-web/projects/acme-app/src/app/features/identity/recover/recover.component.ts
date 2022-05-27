import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-recover',
  templateUrl: './recover.component.html',
  styleUrls: ['./recover.component.less'],
})
export class RecoverComponent {
  recoverForm = this.formBuilder.group({
    email: ['', Validators.required],
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
  @Output() formReset = new EventEmitter();

  requested: boolean = false;

  constructor(private formBuilder: FormBuilder) {}

  reset(event?: MouseEvent) {
    event?.preventDefault();
    this.requested = false;
    this.recoverForm.reset();
    this.formReset.emit();
  }

  onSubmit(data: { email: string }) {
    this.formSubmit.emit(data);
  }
}
