import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <div class="sidebar">
      <div class="logo-container">
        <span class="logo-text">DEV SU BANCO</span>
      </div>
      <nav>
        <ul class="nav-list">
          <li>
            <a routerLink="/clientes" routerLinkActive="active" class="nav-link">
              <span>👤</span> Clientes
            </a>
          </li>
          <li>
            <a routerLink="/cuentas" routerLinkActive="active" class="nav-link">
              <span>💳</span> Cuentas
            </a>
          </li>
          <li>
            <a routerLink="/movimientos" routerLinkActive="active" class="nav-link">
              <span>🔄</span> Movimientos
            </a>
          </li>
          <li>
            <a routerLink="/reportes" routerLinkActive="active" class="nav-link">
              <span>📊</span> Reportes
            </a>
          </li>
        </ul>
      </nav>
    </div>
  `
})
export class SidebarComponent {}
