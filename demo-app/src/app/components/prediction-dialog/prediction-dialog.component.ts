import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CommonModule } from '@angular/common';
import { AiService, PredictionResult } from '../../services/ai.service';

export interface PredictionDialogData {
  personId: string;
  personName: string;
}

@Component({
  selector: 'app-prediction-dialog',
  imports: [MatDialogModule, MatButtonModule, MatProgressSpinnerModule, CommonModule],
  templateUrl: './prediction-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PredictionDialogComponent {
  private readonly dialogRef = inject(MatDialogRef<PredictionDialogComponent>);
  protected readonly data = inject<PredictionDialogData>(MAT_DIALOG_DATA);
  private readonly aiService = inject(AiService);

  protected readonly result = signal<PredictionResult | null>(null);
  protected readonly isLoading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);

  constructor() {
    this.aiService.predictProject(this.data.personId).subscribe({
      next: (res) => {
        this.result.set(res);
        this.isLoading.set(false);
      },
      error: (err) => {
        const msg = err?.error?.error ?? err?.error?.message ?? null;
        if (err.status === 400) {
          this.errorMessage.set('This person has no skills assigned yet.');
        } else {
          this.errorMessage.set('Prediction service is currently unavailable.');
        }
        this.isLoading.set(false);
      },
    });
  }

  protected close(): void {
    this.dialogRef.close();
  }
}
