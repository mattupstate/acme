import { Component, EventEmitter, Output } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-practices-form',
  templateUrl: './practices-form.component.html',
  styleUrls: ['./practices-form.component.less'],
})
export class PracticesFormComponent {
  practiceForm = this.formBuilder.group({
    name: ['', Validators.required],
  });

  @Output() formSubmit = new EventEmitter<{
    name: string;
  }>();

  constructor(private formBuilder: UntypedFormBuilder) {}

  hasError(control: string, validator: string) {
    return this.practiceForm.controls[control].hasError(validator);
  }

  onSubmit(data: { name: string }) {
    this.formSubmit.emit(data);
  }
}
