import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Skill } from '../models/person.model';

const API_URL = 'http://localhost:8080/skills';

export interface CreateSkillDto {
  skillName: string;
}

@Injectable({ providedIn: 'root' })
export class SkillService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<Skill[]> {
    return this.http.get<Skill[]>(API_URL);
  }

  create(dto: CreateSkillDto): Observable<Skill> {
    return this.http.post<Skill>(API_URL, dto);
  }

  update(id: string, dto: CreateSkillDto): Observable<Skill> {
    return this.http.put<Skill>(`${API_URL}/${id}`, dto);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}