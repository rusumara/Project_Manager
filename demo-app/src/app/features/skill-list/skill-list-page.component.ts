import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SkillService } from '../../services/skill.service';

import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';

@Component({
  selector: 'app-skill-list-page',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule
  ],
  templateUrl: './skill-list-page.component.html',
  styleUrls: ['./skill-list-page.component.scss']
})
export class SkillListPageComponent {

  private skillService = inject(SkillService);

  displayedColumns = ['skillName', 'actions'];

  skills: any[] = [];

  isLoading = () => false;
  hasError = () => false;

  constructor() {
    this.loadSkills();
  }

  loadSkills() {
    this.skillService.getAll().subscribe({
      next: (data) => {
        this.skills = data;
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  logout() {}

  openCreateDialog() {

    const skillName = prompt('Enter skill name');

    if (!skillName) return;

    this.skillService.create({ skillName }).subscribe({
      next: () => {
        this.loadSkills();
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  openEditDialog(skill: any) {

    const updatedName = prompt('Edit skill name', skill.skillName);

    if (!updatedName) return;

    this.skillService.update(skill.id, {
      skillName: updatedName
    }).subscribe({
      next: () => {
        this.loadSkills();
      },
      error: (err) => {
        console.error(err);
      }
    });
  }

  openDeleteDialog(skill: any) {

    const confirmed = confirm(
      `Delete ${skill.skillName}?`
    );

    if (!confirmed) return;

    this.skillService.delete(skill.id).subscribe({
      next: () => {
        this.loadSkills();
      },
      error: (err) => {
        console.error(err);
      }
    });
  }
}