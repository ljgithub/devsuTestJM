import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReporteResponse, MovimientoReporte } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/reportes';

  getReporte(fechaRange: string, clienteIdOrCode: string): Observable<MovimientoReporte[]> {
    const params = new HttpParams()
      .set('fecha', fechaRange)
      .set('cliente', clienteIdOrCode);

    return this.http.get<MovimientoReporte[]>(this.apiUrl, { params });
  }

  getPdfReporte(fechaRange: string, clienteIdOrCode: string): Observable<ReporteResponse> {
    const params = new HttpParams()
      .set('fecha', fechaRange)
      .set('cliente', clienteIdOrCode);

    return this.http.get<ReporteResponse>(`${this.apiUrl}/pdf`, { params });
  }
}
