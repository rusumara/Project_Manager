import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';

export interface AssignDialogData {
  title: string;
  label: string;
  items: { id: string; label: string }[];
}

@Component({
  selector: 'app-assign-dialog',
  imports: [CommonModule, FormsModule, MatDialogModule, MatButtonModule, MatSelectModule, MatFormFieldModule],
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <mat-form-field appearance="outline" style="width:100%">
        <mat-label>{{ data.label }}</mat-label>
        <mat-select [(ngModel)]="selectedId">
          @for (item of data.items; track item.id) {
            <mat-option [value]="item.id">{{ item.label }}</mat-option>
          }
        </mat-select>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="cancel()">Cancel</button>
      <button mat-flat-button [disabled]="!selectedId" (click)="confirm()">Assign</button>
    </mat-dialog-actions>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AssignDialogComponent {
  private readonly dialogRef = inject(MatDialogRef<AssignDialogComponent>);
  protected readonly data = inject<AssignDialogData>(MAT_DIALOG_DATA);
  protected selectedId: string | null = null;

  protected confirm(): void {
    this.dialogRef.close(this.selectedId);
  }

  protected cancel(): void {
    this.dialogRef.close(null);
  }
}
