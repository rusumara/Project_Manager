import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectService } from '../../services/project.service';

import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';

@Component({
  selector: 'app-project-list-page',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule
  ],
  templateUrl: './project-list-page.component.html',
  styleUrls: ['./project-list-page.component.scss']
})
export class ProjectListPageComponent {

  private projectService = inject(ProjectService);

  displayedColumns = ['projectName', 'actions'];

  projects: any[] = [];

  isLoading = () => false;
  hasError = () => false;

  constructor() {
    this.loadProjects();
  }

  loadProjects() {
    this.projectService.getAll().subscribe({
      next: (data) => {
        this.projects = data;
      },
      error: (err) => {
        console.error('LOAD ERROR:', err);
      }
    });
  }

  logout() {}

  openCreateDialog() {

    const projectName = prompt('Enter project name');

    if (!projectName) return;

    this.projectService.create({ projectName }).subscribe({
      next: () => {
        this.loadProjects();
      },
      error: (err) => {
        console.error('CREATE ERROR:', err);
      }
    });
  }

  openEditDialog(project: any) {

    const updatedName = prompt(
      'Edit project name',
      project.projectName
    );

    if (!updatedName) return;

    this.projectService.update(project.id, {
      projectName: updatedName
    }).subscribe({
      next: () => {
        this.loadProjects();
      },
      error: (err) => {
        console.error('UPDATE ERROR:', err);
      }
    });
  }

  openDeleteDialog(project: any) {

    const confirmed = confirm(
      `Delete ${project.projectName}?`
    );

    if (!confirmed) return;

    this.projectService.delete(project.id).subscribe({
      next: () => {
        this.loadProjects();
      },
      error: (err) => {
        console.error('DELETE ERROR:', err);
      }
    });
  }
  
}