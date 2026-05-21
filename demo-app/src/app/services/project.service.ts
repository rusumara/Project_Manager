import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project } from '../models/person.model';

const API_URL = 'http://localhost:8080/projects';

export interface CreateProjectDto {
  projectName: string;
}

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private api = 'http://localhost:8080/projects';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<Project[]>(this.api);
  }

  create(project: Project) {
    return this.http.post<Project>(this.api, project);
  }

  update(id: string, project: Project) {
    return this.http.put<Project>(`${this.api}/${id}`, project);
  }

  delete(id: string) {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}