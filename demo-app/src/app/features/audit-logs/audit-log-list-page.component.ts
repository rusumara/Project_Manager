import { ChangeDetectionStrategy, Component, inject, signal, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { LoginStore } from '../login/login.store';
import { AuditLogService } from '../../services/audit-log.service';
import { AuditLog } from '../../models/audit-log.model';

@Component({
  selector: 'app-audit-log-list-page',
  imports: [MatTableModule, MatToolbarModule, MatButtonModule, MatIconModule, DatePipe],
  templateUrl: './audit-log-list-page.component.html',
  styleUrl: './audit-log-list-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AuditLogListPageComponent implements OnInit {
  private readonly auditLogService = inject(AuditLogService);
  private readonly loginStore = inject(LoginStore);
  private readonly router = inject(Router);

  protected readonly logs = signal<AuditLog[]>([]);
  protected readonly isLoading = signal(true);
  protected readonly hasError = signal(false);
  protected readonly displayedColumns = ['action', 'userEmail', 'timestamp'];

  ngOnInit(): void {
    this.auditLogService.getAll().subscribe({
      next: (data) => {
        this.logs.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.hasError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  protected logout(): void {
    this.loginStore.logout();
    void this.router.navigate(['/login']);
  }
}
