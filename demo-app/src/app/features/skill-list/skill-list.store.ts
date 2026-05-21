import { computed, inject, Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { Skill } from '../../models/person.model';
import { CreateSkillDto, SkillService } from '../../services/skill.service';



@Injectable()
export class SkillListStore {
  private readonly service = inject(SkillService);

  private readonly _skills = signal<Skill[]>([]);
  private readonly _isLoading = signal(false);
  private readonly _hasError = signal(false);

  readonly skills = this._skills.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly hasError = this._hasError.asReadonly();

  load() {
    this._isLoading.set(true);

    this.service.getAll().subscribe({
      next: (data) => {
        this._skills.set(data);
        this._isLoading.set(false);
      },
      error: () => {
        this._hasError.set(true);
        this._isLoading.set(false);
      }
    });
  }

  create(skill: Skill) {
    this.service.create(skill).subscribe(() => this.load());
  }

  update(id: string, skill: Skill) {
    this.service.update(id, skill).subscribe(() => this.load());
  }

  delete(id: string) {
    this.service.delete(id).subscribe(() => this.load());
  }
}