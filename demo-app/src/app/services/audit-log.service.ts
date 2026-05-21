import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditLog } from '../models/audit-log.model';

@Injectable({ providedIn: 'root' })
export class AuditLogService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<AuditLog[]> {
    return this.http.get<AuditLog[]>('http://localhost:8080/audit-logs');
  }
}
