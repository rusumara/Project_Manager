import { Injectable, signal } from '@angular/core';

const STORAGE_KEY = 'preferred-theme';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  readonly isDark = signal(false);

  init(): void {
    const stored = localStorage.getItem(STORAGE_KEY);
    const prefersDark = stored
      ? stored === 'dark'
      : window.matchMedia('(prefers-color-scheme: dark)').matches;
    this.apply(prefersDark);
  }

  toggle(): void {
    this.apply(!this.isDark());
  }

  private apply(dark: boolean): void {
    this.isDark.set(dark);
    document.body.style.colorScheme = dark ? 'dark' : 'light';
    localStorage.setItem(STORAGE_KEY, dark ? 'dark' : 'light');
  }
}
