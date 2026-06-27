import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cuenta } from '../models';

@Injectable({
  providedIn: 'root'
})
export class CuentaService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/cuentas';

  getAll(): Observable<Cuenta[]> {
    return this.http.get<Cuenta[]>(this.apiUrl);
  }

  getByNumero(numeroCuenta: string): Observable<Cuenta> {
    return this.http.get<Cuenta>(`${this.apiUrl}/${numeroCuenta}`);
  }

  create(cuenta: Cuenta): Observable<Cuenta> {
    return this.http.post<Cuenta>(this.apiUrl, cuenta);
  }

  update(numeroCuenta: string, cuenta: Cuenta): Observable<Cuenta> {
    return this.http.put<Cuenta>(`${this.apiUrl}/${numeroCuenta}`, cuenta);
  }

  delete(numeroCuenta: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${numeroCuenta}`);
  }
}
