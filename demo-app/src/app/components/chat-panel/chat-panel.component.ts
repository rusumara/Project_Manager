import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  inject,
  signal,
  ViewChild,
  AfterViewChecked,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { AiService } from '../../services/ai.service';

interface ChatMessage {
  role: 'user' | 'bot';
  text: string;
}

@Component({
  selector: 'app-chat-panel',
  imports: [CommonModule, FormsModule, MatButtonModule, MatIconModule, MatInputModule, MatFormFieldModule],
  templateUrl: './chat-panel.component.html',
  styleUrl: './chat-panel.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChatPanelComponent implements AfterViewChecked {
  private readonly aiService = inject(AiService);

  @ViewChild('messageList') private messageList!: ElementRef<HTMLElement>;

  protected readonly isOpen = signal(false);
  protected readonly messages = signal<ChatMessage[]>([]);
  protected readonly inputText = signal('');
  protected readonly isSending = signal(false);

  protected toggle(): void {
    this.isOpen.set(!this.isOpen());
  }

  protected setInput(value: string): void {
    this.inputText.set(value);
  }

  protected send(): void {
    const text = this.inputText().trim();
    if (!text || this.isSending()) return;

    this.messages.update((msgs) => [...msgs, { role: 'user', text }]);
    this.inputText.set('');
    this.isSending.set(true);

    this.aiService.chat(text).subscribe({
      next: (res) => {
        this.messages.update((msgs) => [...msgs, { role: 'bot', text: res.answer }]);
        this.isSending.set(false);
      },
      error: () => {
        this.messages.update((msgs) => [
          ...msgs,
          { role: 'bot', text: 'AI service is currently unavailable. Please try again later.' },
        ]);
        this.isSending.set(false);
      },
    });
  }

  protected onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }

  ngAfterViewChecked(): void {
    if (this.messageList) {
      this.messageList.nativeElement.scrollTop = this.messageList.nativeElement.scrollHeight;
    }
  }
}
