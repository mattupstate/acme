import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HalDocument } from '../../http/hal';
import { PracticesCollectionService } from './practices-collection.service';

@Component({
  selector: 'app-feature-practices-list',
  templateUrl: './practices-list.component.html',
  styleUrls: ['./practices-list.component.less'],
})
export class PracticesListComponent {
  practices: Array<{
    name: string;
    location: string;
    numProviders: number;
  }> = [
    {
      name: 'Practice 1',
      location: '123 Easy Street',
      numProviders: 5,
    },
    {
      name: 'Practice 1',
      location: '123 Easy Street',
      numProviders: 5,
    },
    {
      name: 'Practice 1',
      location: '123 Easy Street',
      numProviders: 5,
    },
    {
      name: 'Practice 1',
      location: '123 Easy Street',
      numProviders: 5,
    },
    {
      name: 'Practice 1',
      location: '123 Easy Street',
      numProviders: 5,
    },
  ];

  addForm: FormGroup = this.formBuilder.group({
    name: ['', Validators.required],
  });

  serverError: boolean = false;
  unexpectedServerError: boolean = false;
  serverErrors: { [key: string]: string } = {};

  constructor(
    private formBuilder: FormBuilder,
    private collectionResource: PracticesCollectionService
  ) {}

  get name() {
    return this.addForm.get('name');
  }

  reset() {
    this.serverError = this.unexpectedServerError = false;
    this.serverErrors = {};
    this.addForm.enable();
  }

  private onSubmitSuccess(response: HalDocument) {
    this.reset();
  }

  private onSubmitError(error: any) {
    this.addForm.enable();
  }

  onSubmit(data: { name: string }) {
    this.addForm.disable();
    this.collectionResource.post(data).subscribe(
      (response) => this.onSubmitSuccess(response),
      (error) => this.onSubmitError(error)
    );
  }
}
