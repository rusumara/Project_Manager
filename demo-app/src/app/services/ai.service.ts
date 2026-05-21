import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PredictionResult {
  personName: string;
  skills: string[];
  projectType: string;
  confidence: number;
}

export interface ChatResult {
  answer: string;
}

@Injectable({ providedIn: 'root' })
export class AiService {
  private readonly http = inject(HttpClient);

  predictProject(personId: string): Observable<PredictionResult> {
    return this.http.post<PredictionResult>(
      `http://localhost:8080/ai/predict/${personId}`,
      {}
    );
  }

  chat(message: string): Observable<ChatResult> {
    return this.http.post<ChatResult>('http://localhost:8080/ai/chat', {
      message,
    });
  }
}
