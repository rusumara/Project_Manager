import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

export interface SkillFormDialogData {
  title: string;
  submitLabel?: string;
  initialValue?: { skillName: string } | null;
}

export interface SkillFormValue {
  skillName: string;
}

export type SkillFormDialogResult = SkillFormValue | undefined;

@Component({
  selector: 'app-skill-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './skill-form-dialog.component.html',
  styleUrl: './skill-form-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SkillFormDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<SkillFormDialogComponent>);
  protected readonly data = inject<SkillFormDialogData>(MAT_DIALOG_DATA);

  protected readonly form = this.fb.nonNullable.group({
    skillName: ['', [Validators.required, Validators.minLength(2)]],
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

    this.dialogRef.close(this.form.getRawValue());
  }

  protected cancel(): void {
    this.dialogRef.close(undefined);
  }
}