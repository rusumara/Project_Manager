import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  inject,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Person } from '../../models/person.model';
import { submit } from '@angular/forms/signals';

export interface ProjectFormDialogData {
  title: string;
  submitLabel?: string;
  initialValue?: ProjectFormInitialValue | null;
  people: Person[];
}

export interface ProjectFormValue {
  projectName: string;
  personId: string;
}

export interface ProjectFormInitialValue {
  projectName: string;
}

export type ProjectFormDialogResult = ProjectFormValue | undefined;

@Component({
  selector: 'app-project-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
  ],
  templateUrl: './project-form-dialog.component.html',
  styleUrl: './project-form-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProjectFormDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<ProjectFormDialogComponent>);
  protected readonly data = inject<ProjectFormDialogData>(MAT_DIALOG_DATA);

  protected readonly form = this.fb.nonNullable.group({
    projectName: ['', [Validators.required, Validators.minLength(2)]],
    personId: ['', [Validators.required]],
  });

  ngOnInit(): void {
    if (this.data.initialValue) {
      this.form.patchValue(this.data.initialValue);
    }
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
   

    const { projectName, personId } = this.form.getRawValue();
    const result: ProjectFormValue = { projectName, personId };

    this.dialogRef.close(result);
  }

  protected cancel(): void {
    this.dialogRef.close(undefined);
  }
}