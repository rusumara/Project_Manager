import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatToolbar } from '@angular/material/toolbar';
import { ConfirmDeleteDialogComponent } from '../../components/confirm-delete-dialog/confirm-delete-dialog.component';
import {
  PersonFormDialogComponent,
  PersonFormDialogData,
  PersonFormDialogResult,
} from '../../components/person-form-dialog/person-form-dialog.component';
import { CreatePersonDto, Person, UpdatePersonDto } from '../../models/person.model';
import { PersonListStore } from './person-list.store';
import { Router } from '@angular/router';
import { LoginStore } from '../login/login.store';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { PredictionDialogComponent, PredictionDialogData } from '../../components/prediction-dialog/prediction-dialog.component';
import { AssignDialogComponent, AssignDialogData } from '../../components/assign-dialog/assign-dialog.component';
import { SkillService } from '../../services/skill.service';
import { ProjectService } from '../../services/project.service';

@Component({
  selector: 'app-person-list-page',
  imports: [MatTableModule, MatButtonModule, MatIconModule, MatDialogModule, MatToolbar, MatSelectModule, CommonModule],
  templateUrl: './person-list-page.component.html',
  styleUrl: './person-list-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PersonListPageComponent {
  private readonly dialog = inject(MatDialog);
  private readonly store = inject(PersonListStore);
  private readonly loginStore = inject(LoginStore);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly skillService = inject(SkillService);
  private readonly projectService = inject(ProjectService);

  protected readonly persons = this.store.persons;
  protected readonly hasError = this.store.hasError;
  protected readonly isLoading = this.store.isLoading;
protected readonly displayedColumns = ['name', 'age', 'email', 'projects', 'skills', 'actions'];
  constructor() {
    this.store.load();
    console.log('ROLE:', this.loginStore.role());
  }
  protected assignSkill(person: Person): void {
    this.skillService.getAll().pipe(takeUntilDestroyed(this.destroyRef)).subscribe((skills) => {
      this.dialog
        .open<AssignDialogComponent, AssignDialogData, string | null>(AssignDialogComponent, {
          data: {
            title: `Assign Skill to ${person.name}`,
            label: 'Skill',
            items: skills.map((s) => ({ id: String(s.id), label: s.skillName ?? '' })),
          },
          width: '320px',
        })
        .afterClosed()
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe((skillId) => {
          if (!skillId) return;
          this.store.assignSkill(person.id, Number(skillId));
        });
    });
  }

  protected assignProject(person: Person): void {
    this.projectService.getAll().pipe(takeUntilDestroyed(this.destroyRef)).subscribe((projects) => {
      this.dialog
        .open<AssignDialogComponent, AssignDialogData, string | null>(AssignDialogComponent, {
          data: {
            title: `Assign Project to ${person.name}`,
            label: 'Project',
            items: projects.map((p) => ({ id: p.id ?? '', label: p.projectName ?? '' })),
          },
          width: '320px',
        })
        .afterClosed()
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe((projectId) => {
          if (!projectId) return;
          this.store.assignProject(person.id, projectId);
        });
    });
  }

  protected isAdmin(): boolean {
  return this.loginStore.role() === 'ADMIN';
}

protected isUser(): boolean {
  return this.loginStore.role() === 'USER';
}

  protected logout(): void {
    this.loginStore.logout();
    void this.router.navigate(['/login']);
  }

  protected goToAuditLogs(): void {
    void this.router.navigate(['/admin/audit-logs']);
  }

  protected openPredictDialog(person: Person): void {
    this.dialog.open<PredictionDialogComponent, PredictionDialogData>(
      PredictionDialogComponent,
      { data: { personId: person.id, personName: person.name }, width: '400px' }
    );
  }

  protected openCreateDialog(): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<PersonFormDialogComponent, PersonFormDialogData, PersonFormDialogResult>(
        PersonFormDialogComponent,
        { data: { title: 'Create Person', submitLabel: 'Create', showPasswordField: true } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((result) => {
        if (!result) return;
        this.store.create(result as CreatePersonDto);
      });
  }

  protected openEditDialog(person: Person): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<PersonFormDialogComponent, PersonFormDialogData, PersonFormDialogResult>(
        PersonFormDialogComponent,
        { data: { title: 'Edit Person', submitLabel: 'Save', initialValue: person } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((result) => {
        if (!result) return;
        this.store.update(person.id, result as UpdatePersonDto);
      });
  }

  protected openDeleteDialog(person: Person): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<ConfirmDeleteDialogComponent, { person: Person }, boolean>(
        ConfirmDeleteDialogComponent,
        { data: { person } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((confirmed) => {
        if (!confirmed) return;
        this.store.remove(person.id);
      });
  }
}
