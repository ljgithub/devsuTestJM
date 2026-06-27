import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReporteService } from '../../services/reporte.service';
import { ClienteService } from '../../services/cliente.service';
import { MovimientoReporte, Cliente } from '../../models';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.component.html'
})
export class ReportesComponent implements OnInit {
  private reporteService = inject(ReporteService);
  private clienteService = inject(ClienteService);

  clientes: Cliente[] = [];
  reporteData: MovimientoReporte[] = [];

  // Filter Models
  selectedClienteId: string = '';
  fechaInicio: string = '';
  fechaFin: string = '';

  base64Pdf: string = '';
  errorMessage: string = '';
  searched: boolean = false;
  isDropdownOpen: boolean = false;

  ngOnInit() {
    this.loadClientes();
    // Default dates (current month)
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    this.fechaInicio = this.formatDate(firstDay);
    this.fechaFin = this.formatDate(now);
  }

  loadClientes() {
    this.clienteService.getAll().subscribe({
      next: (data) => {
        this.clientes = data.filter(c => c.estado);
        if (this.clientes.length > 0) {
          this.selectedClienteId = this.clientes[0].id!.toString();
        }
      }
    });
  }

  buscarReporte() {
    if (!this.selectedClienteId || !this.fechaInicio || !this.fechaFin) {
      this.errorMessage = 'Debe seleccionar un cliente y un rango de fechas completo.';
      return;
    }
    
    this.errorMessage = '';
    const dateRangeParam = `${this.fechaInicio},${this.fechaFin}`;

    this.reporteService.getReporte(dateRangeParam, this.selectedClienteId).subscribe({
      next: (data) => {
        this.reporteData = data;
        this.searched = true;
        
        // Fetch PDF as well for download
        this.reporteService.getPdfReporte(dateRangeParam, this.selectedClienteId).subscribe({
          next: (pdfRes) => {
            this.base64Pdf = pdfRes.pdf;
          },
          error: () => {
            this.base64Pdf = '';
          }
        });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Error al generar el reporte';
        this.reporteData = [];
        this.base64Pdf = '';
        this.searched = true;
      }
    });
  }

  downloadPdf() {
    if (!this.base64Pdf) return;

    try {
      const byteCharacters = atob(this.base64Pdf);
      const byteNumbers = new Array(byteCharacters.length);
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
      }
      const byteArray = new Uint8Array(byteNumbers);
      const blob = new Blob([byteArray], { type: 'application/pdf' });
      
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      
      const clienteNombre = this.clientes.find(c => c.id!.toString() === this.selectedClienteId)?.nombre || 'Cliente';
      a.download = `EstadoCuenta_${clienteNombre.replace(/\s+/g, '_')}_${this.fechaInicio}_${this.fechaFin}.pdf`;
      
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    } catch (e) {
      this.errorMessage = 'Error al procesar el archivo PDF para descarga.';
    }
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  downloadJson() {
    if (this.reporteData.length === 0) return;

    try {
      const jsonString = JSON.stringify(this.reporteData, null, 2);
      const blob = new Blob([jsonString], { type: 'application/json' });
      const url = window.URL.createObjectURL(blob);
      
      const a = document.createElement('a');
      a.href = url;
      
      const clienteNombre = this.clientes.find(c => c.id!.toString() === this.selectedClienteId)?.nombre || 'Cliente';
      a.download = `EstadoCuenta_${clienteNombre.replace(/\s+/g, '_')}_${this.fechaInicio}_${this.fechaFin}.json`;
      
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    } catch (e) {
      this.errorMessage = 'Error al generar el archivo JSON para descarga.';
    }
  }

  private formatDate(date: Date): string {
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  }
}
