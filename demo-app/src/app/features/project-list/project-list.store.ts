import { computed, inject, Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { Project } from '../../models/person.model';
import { CreateProjectDto, ProjectService } from '../../services/project.service';

@Injectable()
export class ProjectListStore {
  private readonly service = inject(ProjectService);

  private readonly _projects = signal<Project[]>([]);
  private readonly _isLoading = signal(false);
  private readonly _hasError = signal(false);

  readonly projects = this._projects.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly hasError = this._hasError.asReadonly();

  constructor() {
    // FIX 1: Automatically fetch the projects as soon as this store is injected/created
    this.load();
  }

  load() {
    this._isLoading.set(true);
    this._hasError.set(false);

    this.service.getAll()
      .pipe(
        finalize(() => {
          this._isLoading.set(false);
        })
      )
      .subscribe({
        next: (data) => {
          this._projects.set(data);
        },
        error: () => {
          this._hasError.set(true);
        }
      });
  }

  // FIX 2: Added the missing create/add functionality
  addProject(dto: CreateProjectDto) {
    this._isLoading.set(true);
    this._hasError.set(false);

    this.service.create(dto)
      .pipe(
        finalize(() => {
          this._isLoading.set(false);
        })
      )
      .subscribe({
        next: (newProject) => {
          // Update the array cleanly using the signal update API
          this._projects.update(currentProjects => [...currentProjects, newProject]);
        },
        error: () => {
          this._hasError.set(true);
        }
      });
  }
}